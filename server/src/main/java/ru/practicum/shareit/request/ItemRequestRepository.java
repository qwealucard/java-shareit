package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterOrderByCreatedDesc(User user);

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findByRequesterIdNot(Long id);
}
