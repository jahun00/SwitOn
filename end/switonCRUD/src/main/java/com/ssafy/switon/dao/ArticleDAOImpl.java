package com.ssafy.switon.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ssafy.switon.dto.Article;
import com.ssafy.switon.dto.ArticleWithLikesDTO;
import com.ssafy.switon.dto.BoardIndexDTO;
import com.ssafy.switon.dto.FeedsIndexDTO;
import com.ssafy.switon.dto.UserStudyDTO;

@Repository
public class ArticleDAOImpl implements ArticleDAO {

	@Autowired
	SqlSession sqlSession;
	
	@Override
	public List<Article> selectArticles() {
		return sqlSession.selectList("article.selectArticles");
	}

	@Override
	public List<Article> selectArticlesByBoardId(int boardId) {
		return sqlSession.selectList("article.selectArticlesByBoardId", boardId);
	}

	@Override
	public List<Article> selectArticlesByUserId(int userId) {
		return sqlSession.selectList("article.selectArticlesByUserId", userId);
	}

	@Override
	public Article selectArticleById(int id) {
		return sqlSession.selectOne("article.selectArticle", id);
	}

	@Override
	public int insertArticle(Article article) {
		return sqlSession.insert("article.insertArticle", article);
	}

	@Override
	public int updateArticle(Article article) {
		return sqlSession.update("article.updateArticle", article);
	}

	@Override
	public int deleteArticle(int id) {
		return sqlSession.delete("article.deleteArticle", id);
	}

	@Override
	public int getRecentArticleIdByUserId(int userId) {
		Integer articleId = sqlSession.selectOne("article.getRecentArticleIdByUserId", userId);
		return articleId = articleId == null ? 0 : articleId;
	}

	@Override
	public List<Article> selectQnasByUserId(int userId) {
		return sqlSession.selectList("article.selectQnAsByUserId", userId);
	}

	@Override
	public List<Article> selectRepositoriesByUserId(int userId) {
		return sqlSession.selectList("article.selectReposByUserId", userId);
	}

	@Override
	public List<Article> selectArticlesByBoardIdLimit5(BoardIndexDTO boardIndexDTO) {
		return sqlSession.selectList("article.selectArticlesByBoardIdLimit5", boardIndexDTO);
	}

	@Override
	public List<Article> selectFeeds(FeedsIndexDTO feedsIndexDTO) {
		return sqlSession.selectList("article.selectFeeds", feedsIndexDTO);
	}

	@Override
	public int cntUserArticlesByStudyId(int user_id, int study_id) {
		UserStudyDTO dto = new UserStudyDTO(user_id, study_id);
		Integer cnt = sqlSession.selectOne("article.cntUserArticlesByStudyId", dto);
		return cnt = cnt == null ? 0 : cnt;
	}

	@Override
	public int selectRecentUserArticleId(int user_id, int study_id) {
		UserStudyDTO dto = new UserStudyDTO(user_id, study_id);
		Integer id = sqlSession.selectOne("article.selectRecentUserArticleId", dto);
		return id = id == null? 0 : id;
	}

	@Override
	public List<ArticleWithLikesDTO> selectTopThreeArticles(int boardId) {
		return sqlSession.selectList("article.selectTopThreeArticles", boardId);
	}

}
