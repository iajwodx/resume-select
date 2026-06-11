package com.resume.mapper;

import com.resume.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Resume data access mapper.
 */
@Mapper
public interface ResumeMapper {

    /**
     * Insert a new resume record.
     *
     * @param resume the resume entity to insert
     * @return affected rows
     */
    int insert(Resume resume);

    /**
     * Update an existing resume record.
     *
     * @param resume the resume entity to update
     * @return affected rows
     */
    int updateById(Resume resume);

    /**
     * Delete a resume record by id.
     *
     * @param id the resume id
     * @return affected rows
     */
    int deleteById(@Param("id") Long id);

    /**
     * Find a resume by id.
     *
     * @param id the resume id
     * @return the resume entity
     */
    Resume selectById(@Param("id") Long id);

    /**
     * Find resumes by name and contact for duplicate detection.
     *
     * @param name    the name to match
     * @param contact the contact to match
     * @return list of matching resumes
     */
    List<Resume> selectByNameAndContact(@Param("name") String name, @Param("contact") String contact);

    /**
     * Update favorite status and fitted position for a resume.
     * Explicitly sets columns (including NULL) — does NOT use dynamic <if> null-skip logic.
     *
     * @param id             the resume id
     * @param isFavorite     favorite status
     * @param fittedPosition fitted position (null → sets DB column to NULL)
     * @return affected rows
     */
    int updateFavorite(@Param("id") Long id,
                       @Param("isFavorite") Boolean isFavorite,
                       @Param("fittedPosition") String fittedPosition,
                       @Param("updateFittedPosition") boolean updateFittedPosition);

    /**
     * Select resumes with filter conditions.
     *
     * @param locations      expected locations (comma separated, match any)
     * @param minWorkYears   minimum work years
     * @param educations     educations (comma separated, match any)
     * @param salaryMin      minimum salary filter
     * @param salaryMax      maximum salary filter
     * @param jobStatus      job status filter
     * @param isFavorite     favorite status filter
     * @param fittedPosition fitted position filter
     * @param offset         page offset
     * @param size           page size
     * @return filtered resume list
     */
    List<Resume> selectWithFilter(@Param("locations") String locations,
                                  @Param("minWorkYears") Integer minWorkYears,
                                  @Param("educations") String educations,
                                  @Param("salaryMin") Integer salaryMin,
                                  @Param("salaryMax") Integer salaryMax,
                                  @Param("jobStatus") String jobStatus,
                                  @Param("isFavorite") Boolean isFavorite,
                                  @Param("fittedPosition") String fittedPosition,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    /**
     * Count resumes matching filter conditions.
     *
     * @param locations      expected locations (comma separated, match any)
     * @param minWorkYears   minimum work years
     * @param educations     educations (comma separated, match any)
     * @param salaryMin      minimum salary filter
     * @param salaryMax      maximum salary filter
     * @param jobStatus      job status filter
     * @param isFavorite     favorite status filter
     * @param fittedPosition fitted position filter
     * @return total count
     */
    int countWithFilter(@Param("locations") String locations,
                        @Param("minWorkYears") Integer minWorkYears,
                        @Param("educations") String educations,
                        @Param("salaryMin") Integer salaryMin,
                        @Param("salaryMax") Integer salaryMax,
                        @Param("jobStatus") String jobStatus,
                        @Param("isFavorite") Boolean isFavorite,
                        @Param("fittedPosition") String fittedPosition);

    /**
     * Select resumes with filter conditions (no pagination, for AI keyword matching).
     *
     * @param locations      expected locations (comma separated, match any)
     * @param minWorkYears   minimum work years
     * @param educations     educations (comma separated, match any)
     * @param salaryMin      minimum salary filter
     * @param salaryMax      maximum salary filter
     * @param jobStatus      job status filter
     * @param isFavorite     favorite status filter
     * @param fittedPosition fitted position filter
     * @return filtered resume list (all results, no limit)
     */
    List<Resume> selectWithFilterNoPaging(@Param("locations") String locations,
                                          @Param("minWorkYears") Integer minWorkYears,
                                          @Param("educations") String educations,
                                          @Param("salaryMin") Integer salaryMin,
                                          @Param("salaryMax") Integer salaryMax,
                                          @Param("jobStatus") String jobStatus,
                                          @Param("isFavorite") Boolean isFavorite,
                                          @Param("fittedPosition") String fittedPosition);
}
