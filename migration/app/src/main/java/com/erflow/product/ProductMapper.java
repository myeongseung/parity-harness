package com.erflow.product;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 제품 매퍼.
 *
 * <p>SQL 은 {@code resources/mapper/product/ProductMapper.xml} 에 있고, 각 구문에
 * 레거시 출처를 주석으로 달아 뒀다.
 *
 * <p>제품 도메인은 아직 이관 전이다. 찾기 팝업이 쓰는 조회 하나만 있다.
 */
@Mapper
public interface ProductMapper {

    /**
     * 찾기 팝업이 쓰는 (코드, 이름) 목록.
     *
     * @return 모든 제품
     */
    List<ProductRow> findAllNames();
}
