package com.kstruct.markdown.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data @ToString(exclude="parent") @EqualsAndHashCode(exclude={"parent"})
public class TocTree {
    private final TocTree parent;
    private final TocEntry details;
    
    private final List<TocTree> children = new ArrayList<>();;
}
