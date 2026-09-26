package com.generated.qualityTrace.validators;

import org.springframework.stereotype.Component;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.utils.ApiException;

/** 不良关闭前置校验：已关闭的记录不允许重复关闭。 */
@Component
public class DefectCloseValidator {

  public void validateClosable(DefectRecord defect) {
    if (DefectDispositionStatus.CLOSED.name().equals(defect.dispositionStatus)) {
      throw new ApiException(409, ErrorCodes.DEFECT_ALREADY_CLOSED, ErrorMessages.DEFECT_ALREADY_CLOSED);
    }
  }
}
