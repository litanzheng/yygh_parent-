package com.tencent.yygh.user.service.impl;

import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.yygh.common.exception.YyghException;
import com.tencent.yygh.common.result.ResultCodeEnum;
import com.tencent.yygh.user.repository.ScheduleRepository;
import com.tencent.yygh.user.service.DepartmentService;
import com.tencent.yygh.user.service.HospitalService;
import com.tencent.yygh.user.service.ScheduleService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private HospitalService hospitalService;
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ScheduleRepository scheduleRepository;

    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        return null;
    }

    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String,Object> result = new HashMap<>();
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (hospital==null){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        IPage iPage = this.getListDate(page,limit,bookingRule);
        List<Data> dataList = iPage.getRecords();
        Criteria criteria =Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)
                .and("workDate").in(dataList);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        AggregationResults<BookingScheduleRuleVo> aggregateResult =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregateResult.getMappedResults();

        //合并数据  map集合 key日期  value预约规则和剩余数量等
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream().
                    collect(
                            Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                                    BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }

        //获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for(int i=0,len=dataList.size();i<len;i++) {
            Date date = (Date) dataList.get(i);
            //从map集合根据key日期获取value值
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            //如果当天没有排班医生
            if(bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期对应星期
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i == len-1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            if(i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if(stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());

        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
//月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
//放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
//停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    @Override
    public Schedule getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        return schedule;
    }

    private String getDayOfWeek(DateTime dateTime) {
        return "";
    }


    //获取可预约日期分页数据
    private IPage getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        Integer cycle = bookingRule.getCycle();
        if (releaseTime.isBeforeNow()){
            cycle++;
        }
        List<Date> listDate = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            DateTime dateTime = new DateTime().plusDays(i);
            String dateString = dateTime.toString("yyyy-MM-dd");
            listDate.add(new DateTime(dateString).toDate());
        }

        List<Date> pagelistDate = new ArrayList<>();
        int start=(page-1)*limit;
        int end=(page-1)*limit+limit;
        //如果记录数少于当前页直接显示
        if (end>listDate.size()){
            end =listDate.size();
        }
        IPage<Date> iPage = new Page<>(page,7,listDate.size());
        iPage.setRecords(pagelistDate);
        return iPage;
    }

    private DateTime getDateTime(Date date,String timeString){
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd")+" "+timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }
}
