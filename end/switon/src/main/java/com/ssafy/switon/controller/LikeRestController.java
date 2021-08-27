package com.ssafy.switon.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.switon.dao.ArticleDAO;
import com.ssafy.switon.dto.Alarm;
import com.ssafy.switon.dto.Article;
import com.ssafy.switon.dto.ArticleFav;
import com.ssafy.switon.dto.ArticleLike;
import com.ssafy.switon.dto.Comment;
import com.ssafy.switon.dto.CommentLike;
import com.ssafy.switon.dto.ReturnMsg;
import com.ssafy.switon.dto.StudyLike;
import com.ssafy.switon.service.AlarmService;
import com.ssafy.switon.service.ArticleFavService;
import com.ssafy.switon.service.ArticleLikeService;
import com.ssafy.switon.service.ArticleService;
import com.ssafy.switon.service.CommentLikeService;
import com.ssafy.switon.service.CommentService;
import com.ssafy.switon.service.JoinService;
import com.ssafy.switon.service.StudyLikeService;
import com.ssafy.switon.service.StudyService;
import com.ssafy.switon.util.JWTUtil;

import io.swagger.annotations.ApiOperation;

@RestController
public class LikeRestController {

	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	StudyLikeService studylikeService;
	
	@Autowired
	ArticleLikeService articlelikeService;
	
	@Autowired
	ArticleService articleService;
	
	@Autowired
	CommentLikeService commentlikeService;
	
	@Autowired
	ArticleFavService articlefavService;
	
	@Autowired
	JoinService joinService;
	
	@Autowired
	SimpMessagingTemplate template;
	
	@Autowired
	ArticleDAO articleDAO;
	
	@Autowired
	StudyService studyService;
	
	@Autowired
	AlarmService alarmService;
	
	@ApiOperation(value = "소모임에 좋아요 누른다.")
	@PostMapping("/study/{studyId}/like")
	public Object studyLike(@PathVariable("studyId") int studyId, HttpServletRequest request) {
			int userId = 0;
			try {
				userId = getUserPK(request);
			} catch(Exception e) {
				return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
			}
			if(!joinService.isMember(studyId, userId)) {
				return new ResponseEntity<>(new ReturnMsg("권한이 없습니다."), HttpStatus.FORBIDDEN);
			}
			StudyLike studylike = studylikeService.searchByUser_Study(userId, studyId);
			
			if(studylike != null) {
				return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 눌렀습니다."), HttpStatus.BAD_REQUEST);
			}
			try {
				studylike = new StudyLike();
				studylike.setStudy_id(studyId);
				studylike.setUser_id(userId);
				if(studylikeService.createStudyLike(studylike)) {
					return new ResponseEntity<>("success", HttpStatus.OK);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ResponseEntity<>("좋아요 누르기 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			
	}
	
	@ApiOperation(value = "소모임에 눌럿던 좋아요를 취소한다.")
	@DeleteMapping("/study/{studyId}/like")
	public Object studyUnLike(@PathVariable("studyId") int studyId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		if(!joinService.isMember(studyId, userId)) {
			return new ResponseEntity<>(new ReturnMsg("권한이 없습니다."), HttpStatus.FORBIDDEN);
		}
		StudyLike studylike = studylikeService.searchByUser_Study(userId, studyId);
		
		if(studylike == null) {
			return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 취소했습니다."), HttpStatus.BAD_REQUEST);
		}
		try {
			if(studylikeService.deleteStudyLikeByUser(userId, studyId)) {
				return new ResponseEntity<>("success", HttpStatus.OK);								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 취소 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "qna게시판 글에 좋아요 누른다.")
	@PostMapping("/study/{studyId}/qna/{articleId}/like")
	public Object studyQnaLike(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, HttpServletRequest request) {
			int userId = 0;
			try {
				userId = getUserPK(request);
			} catch(Exception e) {
				return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
			}
			if(!joinService.isMember(studyId, userId)) {
				return new ResponseEntity<>(new ReturnMsg("권한이 없습니다."), HttpStatus.FORBIDDEN);
			}
			ArticleLike articlelike = articlelikeService.searchByUser_Article(userId, articleId);
			
			if(articlelike != null) {
				return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 눌렀습니다."), HttpStatus.BAD_REQUEST);
			}
			try {
				articlelike = new ArticleLike();
				articlelike.setArticle_id(articleId);
				articlelike.setUser_id(userId);
				if(articlelikeService.createArticleLike(articlelike)) {
					
					//객체를 생성하고 필요한 값을 다넣고 그객체를 보내준다
					String studyName = studyService.search(studyId).getName();
					Article article = articleService.search(articleId);
					Alarm alarm = new Alarm(article.getUser_id(), 2, studyName +" 글에 좋아요", studyId, articleId, 2);
					//template.convertAndSend("/topic/notification/" + articleDAO.selectArticleById(articleId).getUser_id(), alarm);
					alarmService.createAlarm(alarm);
					
					return new ResponseEntity<>("success", HttpStatus.OK);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ResponseEntity<>("좋아요 누르기 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "qna게시판 글에 눌렀던 좋아요를 취소한다")
	@DeleteMapping("/study/{studyId}/qna/{articleId}/like")
	public Object studyQnaUnLike(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		if(!joinService.isMember(studyId, userId)) {
			return new ResponseEntity<>(new ReturnMsg("권한이 없습니다."), HttpStatus.FORBIDDEN);
		}
		ArticleLike articlelike = articlelikeService.searchByUser_Article(userId, articleId);
		
		if(articlelike == null) {
			return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 취소했습니다."), HttpStatus.BAD_REQUEST);
		}
		
		try {
			if(articlelikeService.deleteArticleLikeByUser(userId, articleId)) {
				return new ResponseEntity<>("success", HttpStatus.OK);			
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 취소 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "자료실게시판 글에 좋아요 누른다.")
	@PostMapping("/study/{studyId}/repository/{articleId}/like")
	public Object studyRepoLike(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, HttpServletRequest request) {
			int userId = 0;
			try {
				userId = getUserPK(request);
			} catch(Exception e) {
				return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
			}
			if(!joinService.isMember(studyId, userId)) {
				return new ResponseEntity<>(new ReturnMsg("권한이 없습니다."), HttpStatus.FORBIDDEN);
			}
			ArticleLike articlelike = articlelikeService.searchByUser_Article(userId, articleId);
			if(articlelike != null) {
				return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 눌렀습니다."), HttpStatus.BAD_REQUEST);
			}
			try {
				articlelike = new ArticleLike();
				articlelike.setArticle_id(articleId);
				articlelike.setUser_id(userId);
				if(articlelikeService.createArticleLike(articlelike)) {
					
					String studyName = studyService.search(studyId).getName();
					Article article = articleService.search(articleId);
					Alarm alarm = new Alarm(article.getUser_id(), 2, studyName +" 글에 좋아요", studyId, articleId, 3);
					alarmService.createAlarm(alarm);
					joinService.givePoint(article.getUser_id(), studyId, 2);
					return new ResponseEntity<>("success", HttpStatus.OK);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ResponseEntity<>("좋아요 누르기 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "자료실게시판 글에 눌렀던 좋아요를 취소한다")
	@DeleteMapping("/study/{studyId}/repository/{articleId}/like")
	public Object studyRepoUnLike(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		if(!joinService.isMember(studyId, userId)) {
			return new ResponseEntity<>(new ReturnMsg("권한이 없습니다."), HttpStatus.FORBIDDEN);
		}
		ArticleLike articlelike = articlelikeService.searchByUser_Article(userId, articleId);
		
		if(articlelike == null) {
			return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 취소했습니다."), HttpStatus.BAD_REQUEST);
		}
		
		try {
			if(articlelikeService.deleteArticleLikeByUser(userId, articleId)) {
				return new ResponseEntity<>("success", HttpStatus.OK);								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 취소 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "qna게시판 글 댓글에 좋아요 누른다.")
	@PostMapping("/study/{studyId}/qna/{articleId}/{commentId}/like")
	public Object studyQnaCommentLike(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, @PathVariable("commentId") int commentId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		if(!joinService.isMember(studyId, userId)) {
			return new ResponseEntity<>(new ReturnMsg("권한이 없습니다."), HttpStatus.FORBIDDEN);
		}
		Comment comment = commentService.search(commentId);
		if(comment == null) {
			return new ResponseEntity<>(new ReturnMsg("댓글이 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
		}
		CommentLike commentlike = commentlikeService.searchByUser_Comment(userId, commentId);
		
		if(commentlike != null) {
			return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 눌렀습니다."), HttpStatus.BAD_REQUEST);
		}
		
		try {
			commentlike = new CommentLike();
			commentlike.setComment_id(commentId);
			commentlike.setUser_id(userId);
			if(commentlikeService.create(commentlike)) {
				joinService.givePoint(comment.getUser_id(), studyId, 2);
				
				String studyName = studyService.search(studyId).getName();
				Alarm alarm = new Alarm(comment.getUser_id(), 3, studyName +" 댓글에 좋아요", studyId, articleId, 2);
				alarmService.createAlarm(alarm);
				return new ResponseEntity<>("success", HttpStatus.OK);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 누르기 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "qna게시판 글 댓글에 눌렀던 좋아요를 취소한다")
	@DeleteMapping("/study/{studyId}/qna/{articleId}/{commentId}/like")
	public Object studyQnaCommentUnLike(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, @PathVariable("commentId") int commentId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		if(!joinService.isMember(studyId, userId)) {
			return new ResponseEntity<>(new ReturnMsg("권한이 없습니다."), HttpStatus.FORBIDDEN);
		}
		Comment comment = commentService.search(commentId);
		if(comment == null) {
			return new ResponseEntity<>(new ReturnMsg("댓글이 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
		}
		CommentLike commentlike = commentlikeService.searchByUser_Comment(userId, commentId);
		
		if(commentlike == null) {
			return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 취소했습니다."), HttpStatus.BAD_REQUEST);
		}
		
		try {
			if(commentlikeService.deleteByUser(userId, commentId)) {
				return new ResponseEntity<>("success", HttpStatus.OK);								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 취소 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "자료실게시판 글 댓글에 좋아요 누른다.")
	@PostMapping("/study/{studyId}/repository/{articleId}/{commentId}/like")
	public Object studyRepoCommentLike(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, @PathVariable("commentId") int commentId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		Comment comment = commentService.search(commentId);
		if(comment == null) {
			return new ResponseEntity<>(new ReturnMsg("댓글이 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
		}
		CommentLike commentlike = commentlikeService.searchByUser_Comment(userId, commentId);
		
		if(commentlike != null) {
			return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 눌렀습니다."), HttpStatus.BAD_REQUEST);
		}
		
		try {
			commentlike = new CommentLike();
			commentlike.setComment_id(commentId);
			commentlike.setUser_id(userId);
			if(commentlikeService.create(commentlike)) {
				String studyName = studyService.search(studyId).getName();
				Alarm alarm = new Alarm(comment.getUser_id(), 3, studyName +" 댓글에 좋아요", studyId, articleId, 3);
				alarmService.createAlarm(alarm);
				return new ResponseEntity<>("success", HttpStatus.OK);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 누르기 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "자료실게시판 글 댓글에 눌렀던 좋아요를 취소한다")
	@DeleteMapping("/study/{studyId}/repository/{articleId}/{commentId}/like")
	public Object studyRepoCommentUnLike(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, @PathVariable("commentId") int commentId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		Comment comment = commentService.search(commentId);
		if(comment == null) {
			return new ResponseEntity<>(new ReturnMsg("댓글이 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
		}
		CommentLike commentlike = commentlikeService.searchByUser_Comment(userId, commentId);
		
		if(commentlike == null) {
			return new ResponseEntity<>(new ReturnMsg("이미 좋아요를 취소했습니다."), HttpStatus.BAD_REQUEST);
		}
		
		try {
			if(commentlikeService.deleteByUser(userId, commentId)) {
				return new ResponseEntity<>("success", HttpStatus.OK);								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 취소 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "qna 글에 즐겨찾기 누른다.")
	@PostMapping("/study/{studyId}/qna/{articleId}/fav")
	public Object articleQnAFav(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, HttpServletRequest request) {
			int userId = 0;
			try {
				userId = getUserPK(request);
			} catch(Exception e) {
				return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
			}
			Article article = articleService.search(articleId);
			if(article == null) {
				return new ResponseEntity<>(new ReturnMsg("글이 존재하지 않습니다."), HttpStatus.NOT_FOUND);
			}
			ArticleFav articlefav = articlefavService.searchByUser_Article(userId, articleId);
			
			if(articlefav != null) {
				return new ResponseEntity<>(new ReturnMsg("이미 즐겨찾기한 글입니다."), HttpStatus.BAD_REQUEST);
			}
			
			try {
				articlefav = new ArticleFav();
				articlefav.setArticle_id(articleId);
				articlefav.setUser_id(userId);
				if(articlefavService.create(articlefav)) {
					return new ResponseEntity<>("success", HttpStatus.OK);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ResponseEntity<>("좋아요 누르기 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "자료실 글에 즐겨찾기 누른다.")
	@PostMapping("/study/{studyId}/repository/{articleId}/fav")
	public Object articleRepoFav(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, HttpServletRequest request) {
			int userId = 0;
			try {
				userId = getUserPK(request);
			} catch(Exception e) {
				return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
			}
			ArticleFav articlefav = articlefavService.searchByUser_Article(userId, articleId);
			Article article = articleService.search(articleId);
			if(article == null) {
				return new ResponseEntity<>(new ReturnMsg("글이 존재하지 않습니다."), HttpStatus.NOT_FOUND);
			}
			if(articlefav != null) {
				return new ResponseEntity<>(new ReturnMsg("이미 즐겨찾기한 글입니다."), HttpStatus.BAD_REQUEST);
			}
			try {
				articlefav = new ArticleFav();
				articlefav.setArticle_id(articleId);
				articlefav.setUser_id(userId);
				if(articlefavService.create(articlefav)) {
					return new ResponseEntity<>("success", HttpStatus.OK);
				}				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return new ResponseEntity<>("좋아요 누르기 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "qna 글에 눌렀던 즐겨찾기를 취소한다")
	@DeleteMapping("/study/{studyId}/qna/{articleId}/fav")
	public Object articleQnAUnFav(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		ArticleFav articlefav = articlefavService.searchByUser_Article(userId, articleId);
		Article article = articleService.search(articleId);
		if(article == null) {
			return new ResponseEntity<>(new ReturnMsg("글이 존재하지 않습니다."), HttpStatus.NOT_FOUND);
		}
		if(articlefav == null) {
			return new ResponseEntity<>(new ReturnMsg("이미 즐겨찾기를 해제한 글입니다."), HttpStatus.BAD_REQUEST);
		}
		
		try {
			if(articlefavService.deleteByUser(userId, articleId)) {
				return new ResponseEntity<>("success", HttpStatus.OK);								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 취소 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ApiOperation(value = "자료실 글에 눌렀던 즐겨찾기를 취소한다")
	@DeleteMapping("/study/{studyId}/repository/{articleId}/fav")
	public Object articleRepoUnFav(@PathVariable("studyId") int studyId, @PathVariable("articleId") int articleId, HttpServletRequest request) {
		int userId = 0;
		try {
			userId = getUserPK(request);
		} catch(Exception e) {
			return new ResponseEntity<>(new ReturnMsg("잘못된 접근입니다. 다시 로그인해주세요."), HttpStatus.UNAUTHORIZED);
		}
		ArticleFav articlefav = articlefavService.searchByUser_Article(userId, articleId);
		Article article = articleService.search(articleId);
		if(article == null) {
			return new ResponseEntity<>(new ReturnMsg("글이 존재하지 않습니다."), HttpStatus.NOT_FOUND);
		}
		if(articlefav == null) {
			return new ResponseEntity<>(new ReturnMsg("이미 즐겨찾기를 해제한 글입니다."), HttpStatus.BAD_REQUEST);
		}
		
		try {
			if(articlefavService.deleteByUser(userId, articleId)) {
				return new ResponseEntity<>("success", HttpStatus.OK);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("좋아요 누르기 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private int getUserPK(HttpServletRequest request) {
		return jwtUtil.getUserPK(request.getHeader("Authentication").substring("Bearer ".length()));
	}
	
}
