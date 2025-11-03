package com.audit_service.service;

import com.audit_service.model.Training;
import com.audit_service.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingService {

    private final TrainingRepository trainingRepository;

    public List<Training> getAllTrainings() {
        return (List<Training>) trainingRepository.findAll();
    }

    public Optional<Training> getTrainingById(Long id) {
        return trainingRepository.findById(id);
    }

    public Optional<Training> getTrainingByName(String name) {
        return trainingRepository.findByName(name);
    }

    public List<Training> searchTrainingsByName(String name) {
        return trainingRepository.findByNameContaining(name);
    }

    @Transactional
    public Training createTraining(Training training) {
        training.setCreatedTimestamp(LocalDateTime.now());
        training.setUpdatedTimestamp(LocalDateTime.now());
        if (training.getUpdatedBy() == null) {
            training.setUpdatedBy("SYSTEM");
        }

        Training saved = trainingRepository.save(training);
        return saved;
    }

    @Transactional
    public Training updateTraining(Long id, Training trainingDetails) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found with id: " + id));

        training.setName(trainingDetails.getName());
        training.setDescription(trainingDetails.getDescription());
        training.setUpdatedTimestamp(LocalDateTime.now());
        training.setUpdatedBy(trainingDetails.getUpdatedBy() != null ? trainingDetails.getUpdatedBy() : "SYSTEM");

        Training saved = trainingRepository.save(training);
        return saved;
    }

    @Transactional
    public void deleteTraining(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found with id: " + id));

        String initiator = training.getUpdatedBy() != null ? training.getUpdatedBy() : "SYSTEM";
        trainingRepository.delete(training);
    }
}
