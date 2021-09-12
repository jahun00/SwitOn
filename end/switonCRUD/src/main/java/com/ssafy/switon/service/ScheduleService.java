package com.ssafy.switon.service;

import java.util.List;

import com.ssafy.switon.dto.Schedule;

public interface ScheduleService {
	
	// 모든 스케쥴 조회
	List<Schedule> selectAll();
	// 스터디 아이디로 스케쥴 조회
	Schedule selectScheduleByStudyId(int studyId);
	// id로 스케쥴 상세 정보 조회
	Schedule selectScheduleById(int id);
	// 스케쥴 생성
	boolean createSchedule(Schedule schedule);
	// 스케쥴 수정
	boolean updateSchedule(Schedule schedule);
	// 스케쥴 삭제
	boolean deleteSchedule(int id);
	
}
