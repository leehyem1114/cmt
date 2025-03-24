package com.example.cmtProject.mapper.common;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.dto.comm.CommonCodeDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;

/**
 * 공통코드 관리 매퍼 인터페이스
 */
@Mapper
@Repository
public interface CommonCodeMapper {
    
    /**
     * 공통코드 목록 조회 (Map 반환)
     */
    List<Map<String, Object>> commonList(Map<String, Object> map);
    
    /**
     * 공통코드 상세 목록 조회 (Map 반환)
     */
    List<Map<String, Object>> commonDetailList(Map<String, Object> map);
    
    /**
     * 공통코드 목록 조회 (DTO 반환)
     */
    List<CommonCodeDTO> selectCommonCodes(@Param("keyword") String keyword);
    
    /**
     * 공통코드 단건 조회
     */
    CommonCodeDTO selectCommonCode(@Param("code") String code);
    
    /**
     * 공통코드 등록
     */
    int insertCommonCode(CommonCodeDTO dto);
    
    /**
     * 공통코드 수정
     */
    int updateCommonCode(CommonCodeDTO dto);
    
    /**
     * 공통코드 삭제
     */
    int deleteCommonCode(@Param("code") String code);
    
    /**
     * 상세코드 목록 조회
     */
    List<CommonCodeDetailDTO> selectCommonCodeDetails(
            @Param("commonCode") String commonCode,
            @Param("keyword") String keyword);
    
    /**
     * 상세코드 단건 조회
     */
    CommonCodeDetailDTO selectCommonCodeDetail(
            @Param("commonCode") String commonCode,
            @Param("detailCode") String detailCode);
    
    /**
     * 상세코드 등록
     */
    int insertCommonCodeDetail(CommonCodeDetailDTO dto);
    
    /**
     * 상세코드 수정
     */
    int updateCommonCodeDetail(CommonCodeDetailDTO dto);
    
    /**
     * 상세코드 삭제
     */
    int deleteCommonCodeDetail(
            @Param("commonCode") String commonCode,
            @Param("detailCode") String detailCode);
    
    /**
     * 공통코드에 속한 모든 상세코드 삭제
     */
    int deleteCommonCodeDetailsByCommonCode(@Param("commonCode") String commonCode);
}//CommonCodeMapper
