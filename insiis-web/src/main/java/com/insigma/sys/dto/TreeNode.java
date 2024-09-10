package com.insigma.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jinw
 * @version 2019/1/7
 * <p>epsoft - insiis7</p>
 */
@Data
public class TreeNode<T> implements Serializable {

    private T id;
    private String label;
    private List<TreeNode<T>> children;
    private T parent;

}
