package com.example.tabitabi.DTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.tabitabi.model.Question;
import com.example.tabitabi.model.member.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MyPageMyDataDTO {
	private Member member;
	private List<Question> ql;
	
    public void setQl(List<Question> ql) {
        this.ql = ql;
    }

    public void setQl(Question[] questionsArray) {
        this.ql = new ArrayList<>(Arrays.asList(questionsArray));
    }
}
