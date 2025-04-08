package com.example.cmtProject.mapper.erp.notice;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.notice.NoticeDTO;

@Mapper
public interface NoticeMapper {
	//공지사항 insert
	int insertNotice(NoticeDTO noticeDTO);

}
