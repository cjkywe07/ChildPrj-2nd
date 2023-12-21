package com.project.childprj.controller;

import com.project.childprj.domain.*;
import com.project.childprj.repository.PostRecommendRepository;
import com.project.childprj.service.PostCommentService;
import com.project.childprj.service.PostService;
import com.project.childprj.service.UserService;
import com.project.childprj.util.U;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostCommentService postCommentService;

    @GetMapping("/list")
    public void postList(HttpServletRequest request){
        String uri = U.getRequest().getRequestURI();
        request.getSession().setAttribute("prevPage", uri);
    }

    @GetMapping("/detail/{id}")
    public String marketDetail(@PathVariable(name = "id") Long id, PostRecommend postRecommend, Model model) {
        List<PostComment> list = postCommentService.cmtList(id);
        int recomCnt = postService.recomCnt(id); // 추천수
        boolean check = postService.clickCheck(U.getLoggedUser().getId(), id);
        model.addAttribute("check", check); // 추천 눌렀나?
        model.addAttribute("recommend", recomCnt);
        model.addAttribute("postCmt", list); // 특정 글의 댓글
        model.addAttribute("post", postService.postDetail(id));
        return "post/detail";
    }

    @GetMapping("/write")
    public void postWrite(){
    }

    @GetMapping("/update")
    public void postUpdate(){
    }

    // 댓글 작성
    @PostMapping("/cmtWrite")
    public String marketCmtWrite(PostComment postComment, Model model) {
        Long postId = postComment.getPostId();
        Long userId = U.getLoggedUser().getId();  // 세션 너란 녀석...
        String content = postComment.getContent();

        model.addAttribute("change", postCommentService.cmtWrite(userId, postId, content));
        return "/post/success";
    }

    // 댓글 삭제
    @PostMapping("/cmtDelete")
    public String marketCmtDel(PostComment postComment, Model model) {
        Long cmtId = postComment.getId();
        model.addAttribute("change", postCommentService.cmtRemove(cmtId));
        return "/post/success";
    }

    // 글 삭제
    @PostMapping("/detailDelete")
    public String detailDelete(Post post, Model model) {
        Long postId = post.getId();
        model.addAttribute("change", postService.detailDelete(postId));
        return "/post/success";
    }

    // 추천
    @PostMapping("/recommend")
    public String recommend(Post post, Model model){
        Long postId = post.getId();
        Long userId = U.getLoggedUser().getId();
        postService.recommend(userId, postId);
        return "/post/success";
    }

    // 비추천
    @PostMapping("/opposite")
    public String opposite(Post post, Model model){
        Long postId = post.getId();
        Long userId = U.getLoggedUser().getId();
        postService.opposite(userId, postId);
        return "/post/success";
    }
}
