package ru.otus.sqlmongomigrationtool.shell;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class BatchCommands {
    private final Job importUserJob;
    private final JobLauncher jobLauncher;

    public BatchCommands(Job importUserJob, JobLauncher jobLauncher) {
        this.importUserJob = importUserJob;
        this.jobLauncher = jobLauncher;
    }

    @ShellMethod(value = "startMigrationJobWithJobLauncher", key = "sm-jl")
    public void startMigrationJobWithJobLauncher() throws Exception {
        JobExecution execution = jobLauncher.run(importUserJob, new JobParametersBuilder()
                .toJobParameters());
        System.out.println(execution);
    }
}
