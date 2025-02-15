package zerobase.dividend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zerobase.dividend.exception.impl.NoCompanyException;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.Dividend;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.model.constants.CacheKey;
import zerobase.dividend.persist.entity.CompanyEntity;
import zerobase.dividend.persist.entity.DividendEntity;
import zerobase.dividend.persist.repository.CompanyRepository;
import zerobase.dividend.persist.repository.DividendRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 자주 들어오는가?
    // 자주 변경되는 데이터인가?
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("🟢 @Cacheable 작동 - search company -> " + companyName);

        //1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException()); //에러뜨면 에러 넘김

        //2. 조회된 회사의 아이디로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());


        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());

        log.info("🔵 캐시 저장 - Key: {}", companyName);

        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);
    }
}
