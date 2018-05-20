/* Basic job details */
create table if not exists jms_job (
   job_id integer not null primary key auto_increment,
   name varchar(255) not null,
   job_desc varchar(1000), 
   jar_path varchar(255), 
   schedule_opt char(1),
   schedule_data varchar(100),
   priority integer,
   active_flag char(1)
);

/*Simple list of params only stored */
create table if not exists jms_job_params (
	job_id integer not null,
	param varchar(255) not null	
);

/*Each run of job is identifed as an instance with minimal data */
create table if not exists jms_job_instance (
	instance_id integer not null primary key auto_increment,
	job_id integer not null,
	started_on datetime default null,
	ended_on datetime default null,
	status char(1)	
);