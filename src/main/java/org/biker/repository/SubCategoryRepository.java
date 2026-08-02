package org.biker.repository;

import org.biker.model.Category;
import org.biker.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    List<SubCategory> findByCategory_Id(Long categoryId);

}

