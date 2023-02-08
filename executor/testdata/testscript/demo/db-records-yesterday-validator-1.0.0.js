// Execute sql to validating the number of yesterday's records in the target mysql table.
// (For example, it can be used to check the calculation results of business offline jobs).
function process(context) {
    const dsName = Assert.hasTextOf(context.getArgs()['dsName'], "dsName");
    const tableName = Assert.hasTextOf(context.getArgs()['tableName'], "tableName");
    const sqlWhereBaseConditions = Assert.hasTextOf(context.getArgs()['sqlWhereBaseConditions'], "sqlWhereBaseConditions");
    const updateDateFieldName = Assert.hasTextOf(context.getArgs()['updateDateFieldName'], "updateDateFieldName");
    const updateDateFieldPattern = Assert.hasTextOf(context.getArgs()['updateDateFieldPattern'], "updateDateFieldPattern");
    const assertCountMin = Assert.hasTextOf(context.getArgs()['assertCountMin'], "assertCountMin");
    const assertCountMax = Assert.hasTextOf(context.getArgs()['assertCountMax'], "assertCountMax");

    console.info("Validating DB table of :", dsName, tableName, sqlWhereBaseConditions, updateDateFieldName, updateDateFieldPattern, assertCountMin, assertCountMax);
    try {
        const yesterdayStartDate = DateHolder.getDateOf(5, -1, updateDateFieldPattern);
        const yesterdayEndDate = DateHolder.getDateOf(5, -1, updateDateFieldPattern);
        const sql = "SELECT COUNT(1) AS COUNT FROM " + tableName + " WHERE 1=1 " + sqlWhereBaseConditions 
                        + " and " + updateDateFieldName + ">=" + yesterdayStartDate
                        + " and " + updateDateFieldName + "<=" + yesterdayEndDate;
        const jdbcFacade = context.getDataService().obtainJdbcDSFacade(dsName);
        const result = jdbcFacade.findList(sql, []);
        console.info("Detected for result:", result);

        if (result != null) {
            const count = result['COUNT'];
            if (count >= assertCountMin && count <= assertCountMax) {
                return new ScriptResult(true)
                    .addValue("db_records_status_code", 0)
                    .addValue("db_records_status_result", count)
                    .addValue("db_records_status_desc", "healthy");
            }
        }
    } catch (ex) {
        console.error("Unable to validating DB table for :", dsName, tableName, sqlWhereBaseConditions, updateDateFieldName, updateDateFieldPattern, assertCountMin, assertCountMax, ", reason:", ex);
        return new ScriptResult(false)
            .addValue("db_records_status_code", 1)
            .addValue("db_records_status_result", ex)
            .addValue("db_records_status_desc", "unhealthy");
    }

    return new ScriptResult(false)
        .addValue("db_records_status_code", 2)
        .addValue("db_records_status_desc", "unhealthy");
}