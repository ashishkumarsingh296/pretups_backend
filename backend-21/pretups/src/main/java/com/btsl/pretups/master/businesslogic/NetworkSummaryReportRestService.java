package com.btsl.pretups.master.businesslogic;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.PretupsResponse;

@Path("/networkSummary")
public interface NetworkSummaryReportRestService {

        @POST
        @Path("/download-monthly")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public PretupsResponse<byte[]> downloadMonthly(String requestData ) throws IOException, Exception;




}
