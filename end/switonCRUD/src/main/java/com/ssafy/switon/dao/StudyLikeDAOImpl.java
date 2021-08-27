package com.ssafy.switon.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ssafy.switon.dto.StudyLike;

@Repository
public class StudyLikeDAOImpl implements StudyLikeDAO {

	@Autowired
	SqlSession sqlsession;
	
	@Override
	public List<StudyLike> selectStudyLikes() {
		return sqlsession.selectList("studylike.selectStudyLikes");
	}

	@Override
	public StudyLike selectStudyLike(int id) {
		return sqlsession.selectOne("studylike.selectStudyLike", id);
	}

	@Override
	public int insertStudyLike(StudyLike studylike) {
		return sqlsession.insert("studylike.insertStudyLike", studylike);
	}

	@Override
	public int deleteStudyLike(int id) {
		return sqlsession.delete("studylike.deleteStudyLike", id);
	}

	@Override
	public int selectLikeCount(int study_id) {
		return sqlsession.selectOne("studylike.likeCount", study_id);
	}

	@Override
	public StudyLike selectStudyLikeByUser_Study(int user_id, int study_id) {
		StudyLike studylike = new StudyLike();
		studylike.setUser_id(user_id);
		studylike.setStudy_id(study_id);
		return sqlsession.selectOne("studylike.selectStudyLikeByUser_Study", studylike);
	}

	@Override
	public int deleteStudyLikeByUser(int user_id, int study_id) {
		StudyLike studylike = new StudyLike();
		studylike.setUser_id(user_id);
		studylike.setStudy_id(study_id);
		return sqlsession.delete("studylike.deleteStudyLikeByUser", studylike);
	}

}
