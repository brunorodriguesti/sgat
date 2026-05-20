ALTER TABLE horas_trabalhadas
    CHANGE COLUMN data_trabalho data_relatorio DATE,
    ADD COLUMN data_inicio DATE,
    ADD COLUMN data_fim DATE;