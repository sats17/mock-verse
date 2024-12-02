CREATE TABLE IF NOT EXISTS storage (
    mock_key VARCHAR(255) PRIMARY KEY,
    request_body CLOB,
    response_body CLOB NOT NULL,
    api_path VARCHAR(255) NOT NULL,
    query_parameters VARCHAR(255), -- Correctly specify type as VARCHAR
    content_type VARCHAR(255) NOT NULL
);
