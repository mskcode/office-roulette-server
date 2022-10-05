CREATE TABLE employees
(
    id                    UUID,
    first_name            VARCHAR(128),
    last_name             VARCHAR(128),
    employment_start_time TIMESTAMP WITH TIME ZONE,
    status                VARCHAR(32),

    PRIMARY KEY (id)
);

CREATE INDEX ix_employees_names ON employees (first_name ASC,
                                              last_name ASC);
