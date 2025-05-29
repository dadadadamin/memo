package com.example.demo.dto.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapsRequest {
    private String memoText; // GPT가 분석할 메모 내용
    private String folderLocation; // 💡 fallback으로 사용할 폴더의 장소
}