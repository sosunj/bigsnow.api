package kr.co.bigsnow.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.core.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

@Api(tags = {"1. Main"})
@Slf4j
@RestController

@RequestMapping(path = "/main", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController extends StandardController {

	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	/**
	 * 홈 화면
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/attendRateRankList")
	@Operation(summary = "메뉴 : 메인(홈) 페이지", description = "메인(홈) 페이지의 출석률 강좌 순위를 조회한다.")
	@Parameters({
			@Parameter(name = "fr_dt", required = true, description = "시작일", schema = @Schema(implementation = String.class)),
			@Parameter(name = "to_dt", required = true, description = "죵료일", schema = @Schema(implementation = String.class))

	})


	Map<String, Object> attendRateRankList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
		Map<String, Object> mapReq	    = super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt      = new HashMap();

		Exception rstEx                 = null;

		try{
			// mapReq.put("user_grp_cd" , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
			mapReq.put("cust_no"     , CommonUtil.nvlMap(mapReq, "token_cust_no")   );

			mapReq.put("fr_dt", CommonUtil.removeDateFormat(mapReq, "fr_dt"));
			mapReq.put("to_dt", CommonUtil.removeDateFormat(mapReq, "to_dt"));

			mapRlt.put("attendList"  ,  dbSvc.dbList  ("lesson.mainLessonAttendList" , mapReq)); // Top Attend List

		}catch (Exception ex){
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}



	/**
	 * 홈 화면
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/home")
    @Operation(summary = "메뉴 : 메인(홈) 페이지", description = "메인(홈) 페이지의 정보를 조회한다.")
	@Parameters({
			@Parameter(name = "day_gbn", required = true, description = "오늘:TODAY, 금주:WEEK, 금월:MONTH을 사용한다. ", schema = @Schema(implementation = String.class)),

	      })


	Map<String, Object> home(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	    = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt      = new HashMap();

    	List<Map<String, Object>> lstRs = null;
    	Map<String, Object> mapParam    = new HashMap();
		Map<String, Object> mapInfo     = new HashMap();

        Exception rstEx                 = null;

		int nNum                        = 0;

		try{
           // mapReq.put("user_grp_cd" , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
            mapReq.put("cust_no"     , CommonUtil.nvlMap(mapReq, "token_cust_no")   );

			String strDayGbn = CommonUtil.nvlMap(mapReq, "day_gbn", "WEEK").toUpperCase();
			String strToday  = DateUtil.getCurrentDate();

			switch ( strDayGbn )
			{
				case "TODAY" :

					mapReq.put("fr_dt", strToday);
					mapReq.put("to_dt", strToday);

					mapRlt.put("lessonCntDetail",  dbSvc.dbDetail("lesson.mainLessonScheduleCntDetail" , mapReq)); // 전체수업 / 완료수업 건수
					mapRlt.put("lessonList"     ,  dbSvc.dbList  ("lesson.mainLessonScheduleList"       , mapReq)); // 수업 스케쥴

					mapReq.put("top_row_cnt", 3); // 3개 데이터만

					mapReq.put("sort_gbn", "TOP");
					mapRlt.put("topAttendList"     ,  dbSvc.dbList  ("lesson.mainLessonAttendList"       , mapReq)); // Top Attend List

					mapReq.put("sort_gbn", "WORST");
					mapRlt.put("worstAttendList"     ,  dbSvc.dbList  ("lesson.mainLessonAttendList"       , mapReq)); // Top Attend List

					mapInfo.put("fr_dt", strToday);
					mapInfo.put("to_dt", strToday);
					mapInfo.put("week_nm", DateUtil.getWeekdayName( strToday));

					break;

				case "WEEK" :

					nNum = DateUtil.getWeekdayNum ( strToday );

					mapReq.put("fr_dt",  DateUtil.addDay( strToday, nNum * (-1) ) );
					mapReq.put("to_dt",  DateUtil.addDay( strToday, 7 - nNum) );

					mapRlt.put("lessonCntDetail",  dbSvc.dbDetail("lesson.mainLessonScheduleCntDetail" , mapReq)); // 전체수업 / 완료수업 건수
					mapRlt.put("lessonList"     ,  dbSvc.dbList  ("lesson.mainLessonMonthList"       , mapReq)); // 수업 스케쥴

					mapReq.put("top_row_cnt", 3); // 3개 데이터만
					mapReq.put("sort_gbn", "TOP");
					mapRlt.put("topAttendList"     ,  dbSvc.dbList  ("lesson.mainLessonAttendList"       , mapReq)); // Top Attend List

					mapReq.put("sort_gbn", "WORST");
					mapRlt.put("worstAttendList"     ,  dbSvc.dbList  ("lesson.mainLessonAttendList"       , mapReq)); // Top Attend List

					mapInfo.put("fr_dt", CommonUtil.nvlMap(mapReq, "fr_dt"));
					mapInfo.put("to_dt", CommonUtil.nvlMap(mapReq, "to_dt"));
					mapInfo.put("year_week", DateUtil.WeekNumbering( strToday));

					break;

				case "MONTH" :
					mapReq.put("fr_dt",  strToday.substring(0, 6)  + "01" );
					mapReq.put("to_dt",  strToday.substring(0, 6)  + DateUtil.getLastDay( strToday) );

					break;
			}

            //mapRlt.put("todayLessonList",  dbSvc.dbList("lesson.mainTodayLessonList" , mapReq)); // 오늘의 수업 현황 (최근 5 건)
            //mapRlt.put("todayLessonStat",  dbSvc.dbList("lesson.mainTodayLessonStat" , mapReq)); // ㅁ 전체 수업 현황 (통계 수치-상위분류)
            mapParam.put("brd_kind" ,      "BKD001"); // 공지사항
            mapRlt.put("noticeList"     ,  dbSvc.dbList("board.mainBoardList" , mapParam));

        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


	/**
	 * 홈 화면
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/homeOld")
	@Operation(summary = "메뉴 : 메인(홈) 페이지", description = "메인(홈) 페이지의 정보를 조회한다.")
	Map<String, Object> homeOld(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
		Map<String, Object> mapReq	    = super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt      = new HashMap();

		List<Map<String, Object>> lstRs = null;
		Map<String, Object> mapParam    = new HashMap();

		Exception rstEx                 = null;
		try{
			mapReq.put("user_grp_cd" , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
			mapReq.put("cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no")   );

			mapRlt.put("todayLessonList",  dbSvc.dbList("lesson.mainTodayLessonList" , mapReq)); // 오늘의 수업 현황 (최근 5 건)
			mapRlt.put("todayLessonStat",  dbSvc.dbList("lesson.mainTodayLessonStat" , mapReq)); // ㅁ 전체 수업 현황 (통계 수치-상위분류)
			mapParam.put("brd_kind" ,      "BKD001"); // 공지사항
			mapRlt.put("noticeList"     ,  dbSvc.dbList("board.mainBoardList" , mapParam));

		}catch (Exception ex){
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}



}
