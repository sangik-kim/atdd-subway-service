package nextstep.subway.path.domain;

import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.member.constant.MemberDiscountPolicy;
import nextstep.subway.member.domain.Member;
import nextstep.subway.path.vo.SectionEdge;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;

public class Fare {

    private static final int NOT_CHARGE = 0;
    private static final int PER_CHARGE_DISTANCE = 5;
    private static final int STANDARD_DISTANCE = 10;
    private static final int STANDARD_FARE = 1250;
    private static final int DEDUCTION = 350;

    private final GraphPath<Station, SectionEdge> shortestPath;
    private final Member member;

    public Fare(GraphPath<Station, SectionEdge> shortestPath, Member member) {
        this.shortestPath = shortestPath;
        this.member = member;
    }

    public int calcFare() {
        int distance = (int) shortestPath.getWeight();
        int fare = STANDARD_FARE + calcExtraChargeByLine() + calcAdditionalChargeBy(distance) ;
        return applyToDiscountPolicy(fare);
    }

    private int calcExtraChargeByLine() {
        List<SectionEdge> shortestRouteSectionEdges = shortestPath.getEdgeList();
        return shortestRouteSectionEdges.stream()
                .map(SectionEdge::getLine)
                .filter(line -> line.getExtraCharge() != null)
                .mapToInt(Line::getExtraCharge)
                .max()
                .orElse(NOT_CHARGE);
    }

    private int calcAdditionalChargeBy(int distance) {
        if(distance > STANDARD_DISTANCE){
            return (int) ((Math.ceil((distance - STANDARD_DISTANCE + 1 ) / PER_CHARGE_DISTANCE) + 1) * 100);
        }
        return NOT_CHARGE;
    }

    private int applyToDiscountPolicy(int fare) {
        if(member != null){
            MemberDiscountPolicy memberDiscountPolicy = member.getMemberDiscountPolicy();
            fare = (int) Math.ceil((fare - DEDUCTION) * (1-memberDiscountPolicy.getDiscountPercent()));
        }
        return fare;
    }
}
