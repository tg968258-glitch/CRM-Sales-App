package com.crm.sales_pipeline.dto;
import com.crm.sales_pipeline.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class NotificationDto {
    private Integer notificationId;
    private Long userId;
    private String message;
    private String notificationType;
    private NotificationStatus notificationStatus;

}
