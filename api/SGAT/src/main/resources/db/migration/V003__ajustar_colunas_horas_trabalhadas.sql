ALTER TABLE horas_trabalhadas
    MODIFY COLUMN horas_trabalhadas DECIMAL(6,2),
    ADD COLUMN data_trabalho DATE;
