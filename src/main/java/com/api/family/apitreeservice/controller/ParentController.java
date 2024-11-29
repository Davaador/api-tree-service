package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.dto.customer.ParentDto;
import com.api.family.apitreeservice.model.dto.user.AddParentDto;
import com.api.family.apitreeservice.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @GetMapping("/all")
    public List<CoupleDto> findAllParents() {
        return parentService.findAllParents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CoupleDto addParent(@RequestBody AddParentDto addParentDto) {
        return parentService.addParent(addParentDto);
    }

    @GetMapping
    public ParentDto findParents() {
        return parentService.findParents();
    }

    @GetMapping("{parentId}")
    public ParentDto findParentsByParentId(@PathVariable List<Integer> parentId) {
        return parentService.findParentsByParentId(parentId);
    }
}
