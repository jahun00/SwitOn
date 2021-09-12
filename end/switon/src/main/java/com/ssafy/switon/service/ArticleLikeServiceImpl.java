package com.ssafy.switon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.switon.dao.ArticleLikeDAO;
import com.ssafy.switon.dto.ArticleLike;

@Service
public class ArticleLikeServiceImpl implements ArticleLikeService {

	@Autowired
	ArticleLikeDAO articlelikeDAO;
	
	@Override
	public List<ArticleLike> searchAll() {
		return articlelikeDAO.selectArticleLikes();
	}

	@Override
	public ArticleLike search(int id) {
		return articlelikeDAO.selectArticleLike(id);
	}

	@Override
	public boolean createArticleLike(ArticleLike articlelike) {
		return articlelikeDAO.insertArticleLike(articlelike)==1;
	}

	@Override
	public boolean deleteArticleLike(int id) {
		return articlelikeDAO.deleteArticleLike(id)==1;
	}

	@Override
	public int searchLikeCount(int article_id) {
		return articlelikeDAO.selectLikeCount(article_id);
	}

	@Override
	public ArticleLike searchByUser_Article(int userId, int articleId) {
		return articlelikeDAO.selectArticleLikeByUser_Article(userId, articleId);
	}

	@Override
	public boolean deleteArticleLikeByUser(int user_id, int article_id) {
		return articlelikeDAO.deleteArticleLikeByUser(user_id, article_id)==1;
	}

}
