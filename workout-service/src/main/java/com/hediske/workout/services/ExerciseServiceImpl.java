package com.hediske.workout.services;

import com.hediske.workout.dto.*;
import com.hediske.workout.entities.*;
import com.hediske.workout.enums.ExerciseType;
import com.hediske.workout.enums.MuscleGroup;
import com.hediske.workout.exceptions.EntityNotFoundException;
import com.hediske.workout.exceptions.UnauthorizedAccessException;
import com.hediske.workout.repositories.ExerciseRecordRepository;
import com.hediske.workout.repositories.ExerciseRepository;
import com.hediske.workout.repositories.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;

    @Override
    @Transactional
    public ExerciseDto createExercise(ExerciseDto dto) {
        Exercise exercise = Exercise.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .type(dto.getType())
                .primaryMuscle(dto.getPrimaryMuscle())
                .secondaryMuscles(dto.getSecondaryMuscles())
                .defaultSets(dto.getDefaultSets())
                .defaultReps(dto.getDefaultReps())
                .videoUrl(dto.getVideoUrl())
                .imageUrl(dto.getImageUrl())
                .build();

        Exercise saved = exerciseRepository.save(exercise);
        return mapToExerciseDto(saved);
    }

    @Override
    @Transactional
    public ExerciseDto updateExercise(Long id, ExerciseDto dto) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setType(dto.getType());
        exercise.setPrimaryMuscle(dto.getPrimaryMuscle());
        exercise.setSecondaryMuscles(dto.getSecondaryMuscles());
        exercise.setDefaultSets(dto.getDefaultSets());
        exercise.setDefaultReps(dto.getDefaultReps());
        exercise.setVideoUrl(dto.getVideoUrl());
        exercise.setImageUrl(dto.getImageUrl());

        Exercise updated = exerciseRepository.save(exercise);
        return mapToExerciseDto(updated);
    }

    @Override
    @Transactional
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new EntityNotFoundException("Exercise not found");
        }
        exerciseRepository.deleteById(id);
    }

    @Override
    public Page<ExerciseDto> getAllExercises(Pageable pageable) {
        return exerciseRepository.findAll(pageable)
                .map(this::mapToExerciseDto);
    }

    @Override
    public ExerciseDto getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));
        return mapToExerciseDto(exercise);
    }

    @Override
    public Page<ExerciseDto> searchExercises(String name, ExerciseType type, MuscleGroup muscle, Pageable pageable) {
        if (name != null && type != null && muscle != null) {
            return exerciseRepository
                    .findByNameContainingIgnoreCaseAndTypeAndPrimaryMuscle(name, type, muscle, pageable)
                    .map(this::mapToExerciseDto);
        } else if (name != null && type != null) {
            return exerciseRepository.findByNameContainingIgnoreCaseAndType(name, type, pageable)
                    .map(this::mapToExerciseDto);
        } else if (name != null && muscle != null) {
            return exerciseRepository.findByNameContainingIgnoreCaseAndPrimaryMuscle(name, muscle, pageable)
                    .map(this::mapToExerciseDto);
        } else if (type != null && muscle != null) {
            return exerciseRepository.findByTypeAndPrimaryMuscle(type, muscle, pageable)
                    .map(this::mapToExerciseDto);
        } else if (name != null) {
            return exerciseRepository.findByNameContainingIgnoreCase(name, pageable)
                    .map(this::mapToExerciseDto);
        } else if (type != null) {
            return exerciseRepository.findByType(type, pageable)
                    .map(this::mapToExerciseDto);
        } else if (muscle != null) {
            return exerciseRepository.findByPrimaryMuscle(muscle, pageable)
                    .map(this::mapToExerciseDto);
        } else {
            return getAllExercises(pageable);
        }
    }

    @Override
    @Transactional
    public WorkoutSessionDto startSession(String userEmail, LocalDateTime startTime) {
        WorkoutSession session = WorkoutSession.builder()
                .userEmail(userEmail)
                .startTime(startTime != null ? startTime : LocalDateTime.now())
                .build();

        WorkoutSession saved = workoutSessionRepository.save(session);
        return mapToSessionDto(saved);
    }

    @Override
    @Transactional
    public WorkoutSessionDto endSession(Long sessionId, String userEmail, LocalDateTime endTime) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (!session.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("Unauthorized access to session");
        }

        session.setEndTime(endTime != null ? endTime : LocalDateTime.now());
        WorkoutSession updated = workoutSessionRepository.save(session);
        return mapToSessionDto(updated);
    }

    @Override
    public WorkoutSessionDetailDto getSessionDetails(Long sessionId, String userEmail) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (!session.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("Unauthorized access to session");
        }

        List<ExerciseRecord> records = exerciseRecordRepository.findBySessionId(sessionId);

        return WorkoutSessionDetailDto.builder()
                .session(mapToSessionDto(session))
                .records(records.stream()
                        .map(this::mapToRecordDto)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public Page<WorkoutSessionDto> getUserSessions(String userEmail, Pageable pageable) {
        return workoutSessionRepository.findByUserEmail(userEmail, pageable)
                .map(this::mapToSessionDto);
    }

    @Override
    public List<WorkoutSessionDto> getUserSessionsBetween(String userEmail, LocalDateTime start, LocalDateTime end) {
        return workoutSessionRepository.findByUserEmailAndStartTimeBetween(userEmail, start, end)
                .stream()
                .map(this::mapToSessionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExerciseRecordDto addExerciseRecord(ExerciseRecordDto dto, String userEmail) {
        WorkoutSession session = workoutSessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (!session.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("Unauthorized access to session");
        }

        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        ExerciseRecord record = ExerciseRecord.builder()
                .session(session)
                .exercise(exercise)
                .sets(dto.getSets())
                .reps(dto.getReps())
                .weight(dto.getWeight())
                .notes(dto.getNotes())
                .build();

        ExerciseRecord saved = exerciseRecordRepository.save(record);
        return mapToRecordDto(saved);
    }

    @Override
    @Transactional
    public ExerciseRecordDto updateExerciseRecord(Long recordId, ExerciseRecordDto dto, String userEmail) {
        ExerciseRecord record = exerciseRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise record not found"));

        if (!record.getSession().getUserEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("Unauthorized access to record");
        }

        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        record.setExercise(exercise);
        record.setSets(dto.getSets());
        record.setReps(dto.getReps());
        record.setWeight(dto.getWeight());
        record.setNotes(dto.getNotes());

        ExerciseRecord updated = exerciseRecordRepository.save(record);
        return mapToRecordDto(updated);
    }

    @Override
    @Transactional
    public void deleteExerciseRecord(Long recordId, String userEmail) {
        ExerciseRecord record = exerciseRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise record not found"));

        if (!record.getSession().getUserEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("Unauthorized access to record");
        }

        exerciseRecordRepository.delete(record);
    }

    @Override
    public WorkoutStatsDto getUserWorkoutStats(String userEmail) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        return getUserWorkoutStatsBetween(userEmail, oneMonthAgo, now);
    }

    @Override
    public WorkoutStatsDto getUserWorkoutStatsBetween(String userEmail, LocalDateTime start, LocalDateTime end) {
        List<WorkoutSession> sessions = workoutSessionRepository.findByUserEmailAndStartTimeBetween(userEmail, start,
                end);
        List<ExerciseRecord> records = new ArrayList<>();

        for (WorkoutSession session : sessions) {
            records.addAll(exerciseRecordRepository.findBySessionId(session.getId()));
        }

        // Calculate muscle group distribution
        Map<MuscleGroup, Integer> muscleGroupDistribution = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getExercise().getPrimaryMuscle(),
                        Collectors.summingInt(r -> 1)));

        // Calculate weekly activity
        Map<LocalDate, Integer> weeklyActivity = sessions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStartTime().toLocalDate(),
                        Collectors.summingInt(s -> 1)));

        // Calculate monthly activity
        Map<LocalDate, Integer> monthlyActivity = sessions.stream()
                .collect(Collectors.groupingBy(
                        s -> LocalDate.of(s.getStartTime().getYear(), s.getStartTime().getMonth(), 1),
                        Collectors.summingInt(s -> 1)));

        return WorkoutStatsDto.builder()
                .totalSessions(sessions.size())
                .totalExercises(records.size())
                .muscleGroupDistribution(muscleGroupDistribution)
                .weeklyActivity(weeklyActivity)
                .monthlyActivity(monthlyActivity)
                .build();
    }

    private ExerciseDto mapToExerciseDto(Exercise exercise) {
        return ExerciseDto.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .type(exercise.getType())
                .primaryMuscle(exercise.getPrimaryMuscle())
                .secondaryMuscles(exercise.getSecondaryMuscles())
                .defaultSets(exercise.getDefaultSets())
                .defaultReps(exercise.getDefaultReps())
                .videoUrl(exercise.getVideoUrl())
                .imageUrl(exercise.getImageUrl())
                .build();
    }

    private WorkoutSessionDto mapToSessionDto(WorkoutSession session) {
        return WorkoutSessionDto.builder()
                .id(session.getId())
                .userEmail(session.getUserEmail())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .notes(session.getNotes())
                .build();
    }

    private ExerciseRecordDto mapToRecordDto(ExerciseRecord record) {
        return ExerciseRecordDto.builder()
                .id(record.getId())
                .sessionId(record.getSession().getId())
                .exerciseId(record.getExercise().getId())
                .sets(record.getSets())
                .reps(record.getReps())
                .weight(record.getWeight())
                .notes(record.getNotes())
                .build();
    }

}