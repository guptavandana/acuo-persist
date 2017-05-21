package com.acuo.persist.services;

import com.acuo.persist.entity.Step;
import com.acuo.persist.entity.enums.StatementStatus;

public interface StepService extends Service<Step, String> {

    Step create(StatementStatus status);

}