package com.nomesh.rag_demo.utils;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChunkingService {

//    private final TokenTextSplitter splitter;
//
//    public ChunkingService(TokenTextSplitter splitter) {
//        this.splitter = splitter;
//    }
//
//
//    public List<Document> split(
//            List<Document> documents){
//
//        return splitter.apply(documents);
//    }
}
