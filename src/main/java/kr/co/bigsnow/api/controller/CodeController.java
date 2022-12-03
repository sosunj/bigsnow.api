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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.core.controller.StandardController;
import lombok.extern.slf4j.Slf4j;


// @Api(tags = {"1. User"})
@Slf4j
@RestController
@RequestMapping(path = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings({"unchecked", "rawtypes"})
public class CodeController extends StandardController {

	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	/**
	 * 대표코드 목록 조회
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/codeReprList")
    @Operation(summary = "메뉴 : 시스템 관리 > 코드관리(왼쪽 목록)", description = "대표 공통코드를 조회합니다.")
    public Map<String, Object> codeReprList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt = new HashMap();

    	List<Map<String, Object>> lstRs = null;
		Exception rstEx                 = null;
		try{
			lstRs         = dbSvc.dbList("code.codeReprList" , mapReq);
			int nRowCount = dbSvc.dbInt ("code.codeReprCount", mapReq);

			mapRlt.put("list" , lstRs);
			mapRlt.put("count", nRowCount);
		}catch (Exception ex){
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

	/**
	 * 대표코드 신규 등록 처리
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/insertRepr")
	@Operation(summary = "메뉴 : 시스템 관리 > 코드관리 팝업(대표코드 등록)", description = "대표코드를 등록합니다.")
	@Parameters({
	      @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "repr_nm", required = true, description = "코드명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn", required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "dsc"   , required = false, description = "설명", schema = @Schema(implementation = String.class))

	})
    public Map<String, Object> insertRepr (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {

		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt  = new HashMap();

		Exception rstEx             = null;
		try {
			dbSvc.dbInsert("code.insertRepr", mapReq);
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 대표코드 수정 처리
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/updateRepr")
	@Operation(summary = "메뉴 : 시스템 관리 > 코드관리 팝업(대표코드 수정)", description = "공통코드를 수정합니다.")
	@Parameters({
	      @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "repr_nm", required = true, description = "코드명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "dsc",     required = true, description = "설명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn",  required = true, description = "사용여부", schema = @Schema(implementation = String.class))

	})
    public Map<String, Object> updateRepr (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {

		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt  = new HashMap();

		Exception rstEx             = null;
		try {
			dbSvc.dbInsert("code.updateRepr", mapReq);
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 대표 코드 상세정보 조회
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/reprDetail")
    @Operation(summary = "메뉴 : 시스템 관리 > 코드관리 ( 대표코드 상세조회)", description = "대표 코드를 상세 조회합니다.")
    @Parameters({
     	  @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
    })
    public Map<String, Object> reprDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

		Exception rstEx            = null;
		try {
			mapRlt.put("detail" , dbSvc.dbDetail("code.reprDetail" , mapReq));
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

	/**
	 * 공통코드 신규 등록 처리
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/insertCode")
	@Operation(summary = "메뉴 : 시스템 관리 > 코드관리 팝업(코드 등록)", description = "공통코드를 등록합니다.")
	@Parameters({
	      @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "cd", required = true, description = "코드", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "nm", required = true, description = "코드명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "dsc", required = true, description = "설명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn", required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "val1", required = true, description = "코드값1", schema = @Schema(implementation = String.class))

	})
    public Map<String, Object> insertCode (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {

		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt  = new HashMap();

		Exception rstEx             = null;
		try {
			dbSvc.dbInsert("code.insertCode", mapReq);
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}


	/**
	 * 공통코드 수정 처리
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/updateCode")
	@Operation(summary = "메뉴 : 시스템 관리 > 코드관리 팝업(코드 수정)", description = "공통코드를 수정합니다.")
	@Parameters({
	      @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "cd", required = true, description = "코드", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "nm", required = true, description = "코드명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "dsc", required = true, description = "설명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn", required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "val1", required = true, description = "값1", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> updateCode (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {

		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();

		Exception rstEx             = null;
		try {
			dbSvc.dbInsert("code.updateCode", mapReq);
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 공통코드 상세정보 조회
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/codeDetail")
    @Operation(summary = "메뉴 : 시스템 관리 > 코드관리 ( 코드 상세조회)", description = "공통코드를 상세 조회합니다.")
    @Parameters({
     	  @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
    	, @Parameter(name = "cd", required = true, description = "코드", schema = @Schema(implementation = String.class))
    })
    public Map<String, Object> codeDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt  = new HashMap();

		Exception rstEx             = null;
		try {
			mapRlt.put("detail" , dbSvc.dbDetail("code.codeDetail" , mapReq));
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

	/**
	 * 공통코드 목록 조회
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @GetMapping("/codeList")
    @Operation(summary = "메뉴 : 시스템 관리 > 코드관리(오른쪽 코드목록)", description = "코드 목록을 조회합니다.")
	@Parameters({
		@Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> codeList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt  = new HashMap();

		Exception rstEx             = null;
		try {
			mapRlt.put("list" , dbSvc.dbList("code.codeList" , mapReq));
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

}
