package com.resume.mapper;

import com.resume.entity.UserResumeFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for user-resume favorite relationship.
 */
@Mapper
public interface UserResumeFavoriteMapper {

    /**
     * Add a favorite record.
     */
    int insert(UserResumeFavorite fav);

    /**
     * Remove a favorite record.
     */
    int delete(@Param("userId") Long userId, @Param("resumeId") Long resumeId);

    /**
     * Update fitted position for a favorite.
     */
    int updateFittedPosition(@Param("userId") Long userId,
                             @Param("resumeId") Long resumeId,
                             @Param("fittedPosition") String fittedPosition);

    /**
     * Find a favorite by user and resume.
     */
    UserResumeFavorite selectByUserAndResume(@Param("userId") Long userId,
                                              @Param("resumeId") Long resumeId);

    /**
     * Get distinct fitted positions for a user.
     */
    List<String> selectDistinctPositions(@Param("userId") Long userId);
}
