package com.insigma.sys.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yinjh
 * @version 2021/8/23
 */
@Data
public class LazyTreeNode<T> implements Serializable {

    private T id;

    private String label;

    private T parent;

    private Boolean isLeaf;

    private Object extra;

}
