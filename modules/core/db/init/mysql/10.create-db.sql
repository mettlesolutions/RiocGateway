-- begin RIOC_SPLIT_MAPS
create table RIOC_SPLIT_MAPS (
    ID varchar(32),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    VERSION integer not null,
    --
    name varchar(255),
    submission_id bigint,
    split_information integer,
    splitSubmissionStatus varchar(255),
    --
    primary key (ID)
)^
-- end RIOC_SPLIT_MAPS
-- begin RIOC_ERROR
create table RIOC_ERROR (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    submission_id bigint,
    name varchar(255),
    severity varchar(255),
    code_context varchar(255),
    description longtext,
    error_code varchar(255),
    split_information_moved0 varchar(255),
    split_information integer,
    esmd_transaction_id varchar(255),
    --
    primary key (ID)
)^
-- end RIOC_ERROR
-- begin RIOC_STATUS_CHANGE
create table RIOC_STATUS_CHANGE (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    submission_id bigint,
    name varchar(512),
    result varchar(255),
    split_information_moved0 varchar(255),
    split_information varchar(255),
    esmd_transaction_id varchar(255),
    --
    primary key (ID)
)^
-- end RIOC_STATUS_CHANGE
-- begin RIOC_HIH_CONFIGURATION
create table RIOC_HIH_CONFIGURATION (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    hiHName varchar(255) not null,
    hIIHOid varchar(255) not null,
    testRCOid varchar(255) not null,
    esMDIP varchar(512),
    issuer varchar(255) not null,
    testMode boolean not null,
    payloadThreshold integer not null,
    hIHDescription varchar(512) not null,
    esMDUrl varchar(512) not null,
    --
    primary key (ID)
)^
-- end RIOC_HIH_CONFIGURATION

-- begin RIOC_LINEOF_BUSINESS
create table RIOC_LINEOF_BUSINESS (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    name varchar(512),
    content_type varchar(255),
    is_esmd_claim_mandatory boolean,
    is_esmd_claim_displayed boolean,
    is_esmd_caseid_displayed boolean,
    --
    primary key (ID)
)^
-- end RIOC_LINEOF_BUSINESS
-- begin RIOC_NOTIFICATION_SLOT
create table RIOC_NOTIFICATION_SLOT (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    submission_id bigint,
    name varchar(255),
    value varchar(255),
    split_Information integer,
    --
    primary key (ID)
)^
-- end RIOC_NOTIFICATION_SLOT
-- begin RIOC_SUBMISSION
create table RIOC_SUBMISSION (
    ID bigint,
    --
    patient_address_zip varchar(255),
    last_name varchar(255),
    intended_recepient varchar(32) not null,
    message longtext,
    is_non_claimant boolean,
    first_name varchar(255),
    has_first_witness_signature boolean,
    middle_name varchar(255),
    comments longtext,
    date_of_birth date,
    is_sample boolean,
    patient_address_city varchar(255),
    author_type varchar(50),
    phonenumber varchar(255),
    has_second_witness_signature boolean,
    patient_address varchar(255),
    social_security_number varchar(255),
    patient_address_street varchar(255),
    author_npi varchar(255) not null,
    has_signature_of_patient boolean,
    stage varchar(255),
    name varchar(255) not null,
    purpose_of_submission varchar(32) not null,
    esmd_claim_id varchar(255),
    status varchar(255),
    patient_address_state varchar(255),
    has_message boolean,
    esmd_case_id varchar(255),
    esmd_transaction_id varchar(255),
    highest_split_no integer,
    last_submitted_split integer,
    unique_id_list longtext,
    transaction_id_list varchar(255),
    threshold integer,
    auto_split boolean,
    hIHConfigID varchar(32),
    --
    primary key (ID)
)^
-- end RIOC_SUBMISSION
-- begin RIOC_DOCUMENT
create table RIOC_DOCUMENT (
    ID varchar(32),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    VERSION integer not null,
    --
    title varchar(255),
    comments varchar(512),
    filename varchar(255),
    fileDescriptorID varchar(32),
    submissionID bigint,
    language varchar(50),
    splitNumber integer,
    attachmentcontrolnumber varchar(255),
    document_type varchar(50),
    --
    primary key (ID)
)^
-- end RIOC_DOCUMENT
-- begin RIOC_RECEPIENT
create table RIOC_RECEPIENT (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    name varchar(512),
    oid varchar(512),
    --
    primary key (ID)
)^
-- end RIOC_RECEPIENT
-- begin RIOC_PROVIDERS
create table RIOC_PROVIDERS (
    ID bigint,
    --
    SUBMISSION_ID bigint,
    NAME varchar(255),
    PROVIDER_NPI bigint,
    addr_line1 varchar(255),
    addr_line2 varchar(255),
    city varchar(255),
    state varchar(255),
    zipcode varchar(255),
    registered_for_emdr boolean,
    last_submitted_transaction varchar(50),
    --
    primary key (ID)
)^
-- end RIOC_PROVIDERS
