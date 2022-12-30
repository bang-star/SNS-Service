package com.example.dto.response;

import com.example.model.Alarm;
import com.example.model.AlarmArgs;
import com.example.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmResponse {
    private Integer id;
    private AlarmType alarmType;
    private AlarmArgs alarmArgs;
    private String text;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static AlarmResponse fromAlarm(Alarm alarm) {
        return new AlarmResponse(
            alarm.getId(),
            alarm.getAlarmType(),
            alarm.getArgs(),
            alarm.getAlarmType().getAlarmText(),
            alarm.getRegisteredAt(),
            alarm.getUpdatedAt(),
            alarm.getDeletedAt()
        );
    }
}
