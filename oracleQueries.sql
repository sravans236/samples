create table employees(
  memberid    varchar2(10),
  membername    varchar2(10),
  constraint pk_memberid primary key (memberid)
  );

create or replace NONEDITIONABLE PROCEDURE TEST_INSERT_SP
(
  PARAM1 IN VARCHAR2
, PARAM2 IN VARCHAR2
) AS
BEGIN
  INSERT INTO Employees(memberid, membername) VALUES(PARAM1, PARAM2);
END TEST_INSERT_SP;

select * from Employees;

CREATE OR REPLACE PROCEDURE pr_RebellionRider IS
  var_name VARCHAR2 (30):= 'Kalvala';
  var_web VARCHAR2 (30) := 'web.com';
BEGIN
  DBMS_OUTPUT.PUT_LINE('Whats Up Internet? I am '||var_name||' from '||var_web);
END Pr_RebellionRider;
