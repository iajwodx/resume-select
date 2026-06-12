package com.resume.mapper;

import com.resume.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Resume data access mapper.
 */
@Mapper
public interface ResumeMapper {

    int insert(Resume resume);

    int updateById(Resume resume);

    int deleteById(@Param("id") Long id);

    Resume selectById(@Param("id") Long id);

    List<Resume> selectByNameAndContact(@Param("name") String name, @Param("contact") String contact);

    List<Map<String, Object>> selectWithFilter(@Param("userId") Long userId,
                                                @Param("locations") String locations,
                                                @Param("minWorkYears") Integer minWorkYears,
                                                @Param("educations") String educations,
                                                @Param("salaryMin") Integer salaryMin,
                                                @Param("salaryMax") Integer salaryMax,
                                                @Param("jobStatus") String jobStatus,
                                                @Param("isFavorite") Boolean isFavorite,
                                                @Param("fittedPosition") String fittedPosition,
                                                @Param("offset") int offset,
                                                @Param("size") int size);

    int countWithFilter(@Param("userId") Long userId,
                        @Param("locations") String locations,
                        @Param("minWorkYears") Integer minWorkYears,
                        @Param("educations") String educations,
                        @Param("salaryMin") Integer salaryMin,
                        @Param("salaryMax") Integer salaryMax,
                        @Param("jobStatus") String jobStatus,
                        @Param("isFavorite") Boolean isFavorite,
                        @Param("fittedPosition") String fittedPosition);

    List<Map<String, Object>> selectWithFilterNoPaging(@Param("userId") Long userId,
                                                        @Param("locations") String locations,
                                                        @Param("minWorkYears") Integer minWorkYears,
                                                        @Param("educations") String educations,
                                                        @Param("salaryMin") Integer salaryMin,
                                                        @Param("salaryMax") Integer salaryMax,
                                                        @Param("jobStatus") String jobStatus,
                                                        @Param("isFavorite") Boolean isFavorite,
                                                        @Param("fittedPosition") String fittedPosition);
}
