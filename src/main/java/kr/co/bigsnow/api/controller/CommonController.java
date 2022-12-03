package kr.co.bigsnow.api.controller;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.service.CommonService;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.FileManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/commmon" )
public class CommonController extends StandardController {

	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	@Autowired
	protected CommonService commonService;

	/**
	 * 첨부파일을 업로드 후 DB에 등록한 파일 번호를 반환
	 * @param request
	 * @param response
	 * @param requestFile
	 * @return
	 * @throws JsonProcessingException
	 */
    @PostMapping( value="/fileUpload") /* , consumes = { MediaType.MULTIPART_FORM_DATA_VALUE } */
    @Operation(summary = "공통 : 파일 업로드", description = "첨부파일을 업로드 후 DB에 등록한 파일 번호를 반환한다.")
    @Parameters({
    	  @Parameter(name = "up_file", required = true,  description = "첨부 파일", schema = @Schema(implementation = File.class)  )
    })
    Map<String, Object>  fileUpload (HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile )  throws JsonProcessingException
    {
        Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/file/", false); // requestEntity
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx             = null;
        try {
            FileManager fileMgr = new FileManager(request, dbSvc);
            // mapReq.put("rel_key", CommonUtil.nvlMap(mapReq, "auth_idx"));
            // mapReq.put("rel_tbl", "TB_PROD"); // 테이블 명
            fileMgr.fileDbSave(mapReq);

            mapRlt.put("file_no", CommonUtil.nvlMap(mapReq, "file_no" ));
        }  catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

}

