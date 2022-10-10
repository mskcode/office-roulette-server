-- table for storing employee information
CREATE TABLE employees
(
    id                    UUID                     NOT NULL,
    first_name            VARCHAR(128)             NOT NULL,
    last_name             VARCHAR(128)             NOT NULL,
    employment_start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status                VARCHAR(32)              NOT NULL,

    PRIMARY KEY (id)
);

-- table for storing individual draws
CREATE TABLE draws
(
    id          BIGINT                   NOT NULL,
    status      VARCHAR(32)              NOT NULL,
    insert_time TIMESTAMP WITH TIME ZONE NOT NULL,
    close_time  TIMESTAMP WITH TIME ZONE,

    PRIMARY KEY (id)
);

CREATE SEQUENCE seq_draws_id START WITH 1 INCREMENT BY 1;

-- table for linking employees taking part to a draw
CREATE TABLE draw_employees
(
    draw_id     BIGINT NOT NULL,
    employee_id UUID   NOT NULL,

    PRIMARY KEY (draw_id, employee_id)
);

ALTER TABLE draw_employees
    ADD CONSTRAINT fk_draw_employees_draw_id
        FOREIGN KEY (draw_id) REFERENCES draws (id) ON DELETE CASCADE;

-- no ON DELETE CASCADE since you shouldn't permanently delete employees
-- only deactivate them
ALTER TABLE draw_employees
    ADD CONSTRAINT fk_draw_employees_employee_id
        FOREIGN KEY (employee_id) REFERENCES employees (id);

-- table for storing draw results
CREATE TABLE draw_results
(
    draw_id            BIGINT                   NOT NULL,
    winner_employee_id UUID                     NOT NULL,
    result_insert_time TIMESTAMP WITH TIME ZONE NOT NULL,

    PRIMARY KEY (draw_id)
);

ALTER TABLE draw_results
    ADD CONSTRAINT fk_draw_results_draw_id
        FOREIGN KEY (draw_id) REFERENCES draws (id) ON DELETE CASCADE;

-- no ON DELETE CASCADE since you shouldn't permanently delete employees
-- only deactivate them
ALTER TABLE draw_results
    ADD CONSTRAINT fk_draw_results_winner_employee_id
        FOREIGN KEY (winner_employee_id) REFERENCES employees (id);
