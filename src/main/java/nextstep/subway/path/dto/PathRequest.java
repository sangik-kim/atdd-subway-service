package nextstep.subway.path.dto;

public class PathRequest {
    private Long source;
    private Long target;

    public PathRequest(Long source, Long target) {
        this.source = source;
        this.target = target;
    }

    public Long getSourceStationId() {
        return source;
    }

    public Long getTargetStationId() {
        return target;
    }
}
