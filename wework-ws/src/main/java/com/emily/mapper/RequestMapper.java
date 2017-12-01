package com.emily.mapper;

import com.emily.model.ThumbRequest;
import com.emily.params.ThumbParameter;
import org.mapstruct.Mapper;

/**
 * Created by lianhao on 2017/11/12.
 */
@Mapper(componentModel = "spring")
public interface RequestMapper {

    ThumbParameter toParam(ThumbRequest request);
}
