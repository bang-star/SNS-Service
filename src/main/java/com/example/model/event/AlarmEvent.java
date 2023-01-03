package com.example.model.event;

import com.example.model.AlarmArgs;
import com.example.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmEvent {

    private Integer receiveUserId;
    private AlarmType alarmType;
    private AlarmArgs args;
}
