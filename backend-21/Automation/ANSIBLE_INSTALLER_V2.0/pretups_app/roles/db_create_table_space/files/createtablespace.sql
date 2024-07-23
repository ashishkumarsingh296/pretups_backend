spool on;
spool pretups_table_space;

create tablespace  {{ PRTP_DATA_TS_NAME }} datafile '{{ PRTP_DATA_TS_FILE }}' size 2048m ;
create tablespace  {{ P_C2SINDX_TS_NAME }}  datafile '{{ P_C2SINDX_TS_FILE }}' size {{ initial_Data_File_size }}  uniform size 2m;
create tablespace  {{ P_C2SDATA_TS_NAME }}   datafile '{{ P_C2SDATA_TS_FILE }}'  size {{ initial_Data_File_size }} uniform size 2m; 
create tablespace  {{ MISP_DATA_TS_NAME }}   datafile '{{ MISP_DATA_TS_FILE }}' size {{ initial_Data_File_size }} uniform size 2m;  
create tablespace  {{ PRTPUSERS_TS_NAME }}  datafile '{{ PRTPUSERS_TS_FILE }}' size {{ initial_Data_File_size }} ;
create tablespace  {{ P_C2SINDX1_TS_NAME }}  datafile '{{ P_C2SINDX1_TS_FILE }}' size {{ initial_Data_File_size }}  uniform size 2m;   
create tablespace  {{ PRTPUSERBAL_HIST_TS_NAME }}  datafile '{{ PRTPUSERBAL_HIST_TS_FILE }}' size {{ initial_Data_File_size }} uniform size 2m;   
create tablespace  {{ PRTPINDX_1_TS_NAME }}  datafile '{{ PRTPINDX_1_TS_FILE }}' size {{ initial_Data_File_size }}  uniform size 2m;    
create tablespace  {{ P_C2SDATA1_TS_NAME }}  datafile '{{ P_C2SDATA1_TS_FILE }}' size {{ initial_Data_File_size }} uniform size 2m;
create tablespace  {{ P2P_INDX_TS_NAME }}  datafile '{{ P2P_INDX_TS_FILE }}' size {{ initial_Data_File_size }} ; 
create tablespace  {{ PRTPMIS_TS_NAME }}  datafile '{{ PRTPMIS_TS_FILE }}' size {{ initial_Data_File_size }} uniform size 2m;      
create tablespace  {{ P2P_DATA_TS_NAME }}  datafile '{{ P2P_DATA_TS_FILE }}' size {{ initial_Data_File_size }};      
create tablespace  {{ IN_INDX_TS_NAME }}  datafile '{{ IN_INDX_TS_FILE }}' size {{ initial_Data_File_size }} uniform size 2m  ;     
create tablespace  {{ PRTPDATA_TS_NAME }}  datafile '{{ PRTPDATA_TS_FILE }}' size {{ initial_Data_File_size }};     
create tablespace  {{ PRTP_INDX_TS_NAME }}  datafile '{{ PRTP_INDX_TS_FILE }}' size {{ initial_Data_File_size }} uniform size 2m;     
create tablespace  {{ IN_DATA_TS_NAME }} datafile '{{ IN_DATA_TS_FILE }}' size {{ initial_Data_File_size }};

spool off;

