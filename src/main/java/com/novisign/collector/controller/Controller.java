package com.novisign.collector.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.novisign.collector.exception.BadRequestException;
import com.novisign.collector.service.SqsMessageSender;
import com.novisign.collector.service.StatPlayerInfoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.utils.StringUtils;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Controller {
  final static String SECRET = "JEO4DmXNNwaaRlKPc76wrY";
  
  private final StatPlayerInfoService statPlayerInfoService;
  private final SqsMessageSender sqsMessageSender;
  
  @ExceptionHandler(Exception.class)
  ResponseEntity<String> exceptionHandler(Exception ex, HttpServletRequest request) {
    log.error("Request {} failed", request.getRequestURI(), ex);
    if (ex instanceof BadRequestException) {
      return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @GetMapping(path = "/getServerStatus", produces = MediaType.TEXT_PLAIN_VALUE)
  public String getServerStatus() {
    return "status: ok";
  }
  
  @PutMapping("/report/submit/media")
  public void submitMediaReport (
    @RequestBody byte[] body,
    @RequestParam(name = "rid") String playerId,
    @RequestParam(name = "tk") String token,
    @RequestParam(name = "s") String salt) {
    validateRequest(playerId, token, salt);
    String reportId = System.currentTimeMillis() + "_" + playerId + "_" + System.nanoTime() + ".mrps.gz";
    sqsMessageSender.sendMessage(reportId, body);
  }

  @PutMapping("/monitor/submit/info")
  public void submitStatPlayerInfo(
    HttpServletRequest request,
    @RequestBody String csv,
    @RequestParam(name = "rid") String playerId,
    @RequestParam(name = "tk") String token,
    @RequestParam(name = "s") String salt) {
    validateRequest(playerId, token, salt);
    if (StringUtils.isEmpty(csv)) {
      throw new BadRequestException("Request body is empty");
    }
    if (csv.charAt(0) == 'i') {
      statPlayerInfoService.create(csv.trim(), getRemoteAddress(request));
    } else if (csv.charAt(0) == 'u') {
      statPlayerInfoService.update(csv.trim(), getRemoteAddress(request));
    } else {
      throw new BadRequestException("Incorrect request body: '" + csv + "'");
    }
  }

  private String getRemoteAddress(HttpServletRequest request) {
    return request.getRemoteAddr() == null ? null : request.getRemoteAddr();
  }

  @PutMapping("/monitor/submit/status")
  public void updateStatPlayerInfoStatus(
    HttpServletRequest request,
    @RequestParam(name = "rid") String playerId,
    @RequestParam(name = "tk") String token,
    @RequestParam(name = "s") String salt,
    @RequestParam String status) {
    validateRequest(playerId, token, salt);
    statPlayerInfoService.updateStatus(playerId, status, getRemoteAddress(request));
  }
  
  private void validateRequest(String playerId, String token, String salt) {
    String expected = DigestUtils.md5Hex(salt + playerId + SECRET);
    if (!token.equals(expected)) {
      throw new BadRequestException("Unauthorized");
    }
  }
}
