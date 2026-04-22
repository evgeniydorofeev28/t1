DROP TABLE IF EXISTS stat_activity;
DROP TABLE IF EXISTS stat_player_info;
DROP TABLE IF EXISTS stat_media;
DROP TABLE IF EXISTS clicks_global_count;
DROP TABLE IF EXISTS stat_event;
DROP TABLE IF EXISTS screen;

CREATE TABLE stat_activity (
    player_key     varchar(36) DEFAULT NULL,
    activity_date  datetime    DEFAULT NULL,
    remote_ip_addr varchar(15) DEFAULT NULL,
    player_id      varchar(80) DEFAULT NULL,
    player_status  varchar(80) DEFAULT NULL
);


CREATE TABLE IF NOT EXISTS stat_player_info (
    player_id                  VARCHAR(80)  NOT NULL,
    player_name                VARCHAR(255) NULL,
    screen_key                 VARCHAR(40)  NULL,
    company_key                VARCHAR(40)  NULL,
    user_login_name            VARCHAR(100) NULL,
    partner_key                VARCHAR(40)  NULL,
    partner_name               VARCHAR(100) NULL,
    player_status              VARCHAR(80)  NULL,
    device_name                VARCHAR(255) NULL,
    os_name                    VARCHAR(255) NULL,
    os_version                 VARCHAR(80)  NULL,
    player_version             VARCHAR(80)  NULL,
    player_brand               VARCHAR(80)  NULL,
    player_type                VARCHAR(80)  NULL,
    internal_address           VARCHAR(80)  NULL,
    external_address           VARCHAR(80)  NULL,
    last_update_time           BIGINT,
    disable_screen_detect_time BIGINT,
    created                    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    player_state               MEDIUMTEXT   NULL,
    custom_extra_info          TEXT         NULL,
    install_id                 VARCHAR(36)  NULL,
    install_source             VARCHAR(40)  NULL,
    auto_upgrade               BOOLEAN   NULL,
    addit_info                 VARCHAR(256)  NULL,
    PRIMARY KEY (player_id)
);


CREATE TABLE IF NOT EXISTS stat_media (
    report_id               VARCHAR(130) NULL,
    player_id               VARCHAR(80)  NOT NULL,
    screen_key              VARCHAR(40)  NOT NULL,
    main_playlist_key       VARCHAR(40)  NULL,
    playlist_key            VARCHAR(40)  NULL,
    creative_key            VARCHAR(45)  NOT NULL,
    media_key               VARCHAR(45)  NULL,
    widget_name             VARCHAR(100) NOT NULL,
    timezone_id             VARCHAR(60)  NOT NULL,
    timezone_offset         INTEGER,
    play_duration           BIGINT       NOT NULL,
    play_count              BIGINT       NULL,
    click_count             BIGINT       NULL,
    height                  INT          NULL,
    width                   INT          NULL,
    entry_report_time       BIGINT       NOT NULL COMMENT 'last report time (note entry duration and count is cumulative over undetermined period)',
    entry_report_local_time BIGINT       NOT NULL COMMENT 'entry_report_time + timezone_offset',
    client_report_time      BIGINT       NOT NULL COMMENT 'UTC time on reporter system',
    server_report_time      BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS stat_event (
    report_id   VARCHAR(130) NULL DEFAULT NULL,
    screen_key  VARCHAR(40)  NOT NULL,
    event_key   VARCHAR(40)  NOT NULL,
    event_type  VARCHAR(150) NOT NULL,
    count       BIGINT   NOT NULL,
    report_time BIGINT   NOT NULL
);


CREATE TABLE IF NOT EXISTS s3_request_activity (
    request_id  CHAR(50)    NOT NULL,
    time        DATETIME    NOT NULL,
    remote_ip   CHAR(45)    NOT NULL,
    object_key  CHAR(100)   NOT NULL,
    object_size BIGINT      NOT NULL,
    bytes_sent  BIGINT      NOT NULL,
    http_status SMALLINT    NOT NULL,
    total_time  INT         NOT NULL,
    source      TINYTEXT    NOT NULL,
    PRIMARY KEY (request_id)
);

CREATE TABLE clicks_global_count (
    company_key    CHAR(45)       NOT NULL,
    aggregate_name VARCHAR(100)   NOT NULL,
    count          BIGINT         NOT NULL,
    modified       BIGINT         NOT NULL,
    PRIMARY KEY (company_key, aggregate_name)
);

CREATE TABLE screen (
    screen_key    CHAR(45)  PRIMARY KEY,
    company_key   CHAR(45)  NOT NULL
);
