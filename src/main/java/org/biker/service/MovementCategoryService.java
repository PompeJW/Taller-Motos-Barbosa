package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.model.MovementCategory;
import org.biker.repository.MovementCategoryRepository;
import org.biker.utils.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovementCategoryService {

    private final MovementCategoryRepository movementCategoryRepository;

    public List<MovementCategory> findAll() {
        return movementCategoryRepository.findAll();
    }

    public MovementCategory findById(Long id) {
        return movementCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.MOVEMENT_CATEGORY_NOT_FOUND));
    }
}
