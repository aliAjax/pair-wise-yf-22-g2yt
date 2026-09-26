package com.generated.qualityTrace.validators;

import org.springframework.stereotype.Component;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.utils.ApiException;

/** 完工收尾提交前置校验：只有生产中 / 已暂停的工单允许提交收尾。 */
@Component
public class WorkOrderCloseoutValidator {

  public void validateSubmittable(WorkOrder order) {
    String status = order.status;
    boolean submittable = WorkOrderStatus.RUNNING.name().equals(status)
        || WorkOrderStatus.PAUSED.name().equals(status);
    if (!submittable) {
      throw new ApiException(409, ErrorCodes.CLOSEOUT_INVALID_STATE,
          ErrorMessages.CLOSEOUT_INVALID_STATE + status);
    }
  }
}
