package com.example.backend.services;

import com.example.backend.entities.Function;
import com.example.backend.entities.RepRapport;

import java.math.BigInteger;
import java.util.List;

public interface FunctionService {
    Function addFunction(Function f);
    List<Function> getListFunction();
    void deleteFunc(Long id);
    Function updateFunction(long id, Function function);


    void assignRepRapportToFunction(Long functionId, Long repRapportId);

    List<RepRapport> getListRapport();

    List<RepRapport> getRepRapportsByFunctionId(Long functionId);

    void removeRepRapportFromFunction(Long functionId, Long repRapportId);
    List<BigInteger> findReportsByFunctionId(Long functionId);

}
