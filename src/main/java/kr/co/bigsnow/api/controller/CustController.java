package kr.co.bigsnow.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController

@RequestMapping(path = "/cust", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustController extends StandardController {

	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	/**
	 * SelectBox 구성을 위한 조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @GetMapping("/custSelectBoxList" )
    @Operation(summary = "기관조회 ", description = "기관의 SelectBox용으로 조회합니다.")
    @Parameters({
		@Parameter(name = "cust_no", required = false, description = "로그인한 기관번호", schema = @Schema(implementation = String.class))
    })
    public Map<String, Object> custSelectBoxList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt = new HashMap();

    	List<Map<String, Object>> lstRs = null;
		Exception rstEx            = null;
		try {
			mapReq.put("user_grp_cd" , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
			mapReq.put("cust_no" ,     CommonUtil.nvlMap(mapReq, "token_cust_no")   );

			lstRs         = dbSvc.dbList("cust.custSelectBoxList" , mapReq);

			mapRlt.put("list" , lstRs);

			if ( lstRs  != null ) {
				mapRlt.put("count", lstRs.size());
			}
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

	/**
	 * 기관 목록 조회
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @PostMapping("/custList" )
    @Operation(summary = "메뉴: 시스템관리 > 기관", description = "각종 기관의 목록을 조회합니다.")
    @Parameters({
 	  @Parameter(name = "cust_nm", required = false, description = "기관명", schema = @Schema(implementation = String.class))
 	, @Parameter(name = "page_now", required = false, description = "현재 페이지 번호(값을 넣지 않으면 전체 목록을 조회한다.)", schema = @Schema(implementation = Integer.class))
 	, @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
	public Map<String, Object> custList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt = new HashMap();

		Exception rstEx            = null;
		try {
			if ( !"".equals(CommonUtil.nvlMap(mapReq, "page_now"))) {
				CommonUtil.setPageParam(mapReq); // Paging 값 세팅
			}

			mapRlt.put("list" , dbSvc.dbList("cust.custList" , mapReq));
			mapRlt.put("count", dbSvc.dbInt ("cust.custCount", mapReq));
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 기관관리(시스템관리)에서 Tree목록 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/custTreeList" )
    @Operation(summary = "메뉴: 시스템관리 > 기관", description = "각종 기관의 Tree목록을 조회합니다.")
	public Map<String, Object> custTreeList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

		Exception rstEx            = null;
		try {
			mapRlt.put("list" , dbSvc.dbList("cust.custTreeList" , mapReq));
			mapRlt.put("count", dbSvc.dbInt ("cust.custTreeCount", mapReq));
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 기관 상세정보 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/custDetail")
    @Operation(summary = "메뉴 : 시스템관리 > 기관 : 기관 상세", description = "기관의 상세정보를 조회한다.")
	@Parameters({
		@Parameter(name = "cust_no", required = true, description = "기관 번호", schema = @Schema(implementation = Integer.class))
	})
	public Map<String, Object> custDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

		Exception rstEx            = null;
		try {
			mapRlt.put("detail" , dbSvc.dbDetail("cust.custDetail" , mapReq));
			mapRlt.put("cust_user" , dbSvc.dbDetail("cust.custUserDetail" , mapReq));
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 기관 정보 신규 등록
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
	@Operation(summary = "메뉴 : 시스템관리 > 기관 등록", description = "기관정보를 저장등록한다" )
	@Parameters({
		   @Parameter(name = "cust_nm"      , required = true, description = "기관명", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "up_cust_no"     , required = false, description = "상위기관번호", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "tel_no"          , required = false, description = "전화번호", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "fax_no"         , required = false, description = "팩스번호", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "post_no"      , required = false, description = "우편번호", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "addr"         , required = false, description = "주소", schema = @Schema(implementation = String.class))
	    ,  @Parameter(name = "addr_detail"  , required = false, description = "상세 주소", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "use_yn"       , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	})
    @RequestMapping(value = "/insertCust", method = RequestMethod.POST )
    public Map<String, Object> insertCust(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

		Exception rstEx            = null;
		try {
			dbSvc.dbInsert("cust.insertCust", mapReq);
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 기관정보 수정
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @RequestMapping(value = "/updateCust", method = RequestMethod.POST )
    public Map<String, Object> updateCust(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

		Exception rstEx            = null;
		try {
			dbSvc.dbInsert("cust.updateCust", mapReq);
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}

}
