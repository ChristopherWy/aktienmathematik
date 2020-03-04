package de.aktienmathematik.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link de.aktienmathematik.domain.Aktien} entity. This class is used
 * in {@link de.aktienmathematik.web.rest.AktienResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /aktiens?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AktienCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter symbol;

    private InstantFilter date;

    private DoubleFilter open;

    private DoubleFilter close;

    private DoubleFilter high;

    private DoubleFilter low;

    private IntegerFilter volume;

    public AktienCriteria() {
    }

    public AktienCriteria(AktienCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.symbol = other.symbol == null ? null : other.symbol.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.open = other.open == null ? null : other.open.copy();
        this.close = other.close == null ? null : other.close.copy();
        this.high = other.high == null ? null : other.high.copy();
        this.low = other.low == null ? null : other.low.copy();
        this.volume = other.volume == null ? null : other.volume.copy();
    }

    @Override
    public AktienCriteria copy() {
        return new AktienCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSymbol() {
        return symbol;
    }

    public void setSymbol(StringFilter symbol) {
        this.symbol = symbol;
    }

    public InstantFilter getDate() {
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public DoubleFilter getOpen() {
        return open;
    }

    public void setOpen(DoubleFilter open) {
        this.open = open;
    }

    public DoubleFilter getClose() {
        return close;
    }

    public void setClose(DoubleFilter close) {
        this.close = close;
    }

    public DoubleFilter getHigh() {
        return high;
    }

    public void setHigh(DoubleFilter high) {
        this.high = high;
    }

    public DoubleFilter getLow() {
        return low;
    }

    public void setLow(DoubleFilter low) {
        this.low = low;
    }

    public IntegerFilter getVolume() {
        return volume;
    }

    public void setVolume(IntegerFilter volume) {
        this.volume = volume;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AktienCriteria that = (AktienCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(symbol, that.symbol) &&
            Objects.equals(date, that.date) &&
            Objects.equals(open, that.open) &&
            Objects.equals(close, that.close) &&
            Objects.equals(high, that.high) &&
            Objects.equals(low, that.low) &&
            Objects.equals(volume, that.volume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        symbol,
        date,
        open,
        close,
        high,
        low,
        volume
        );
    }

    @Override
    public String toString() {
        return "AktienCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (symbol != null ? "symbol=" + symbol + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (open != null ? "open=" + open + ", " : "") +
                (close != null ? "close=" + close + ", " : "") +
                (high != null ? "high=" + high + ", " : "") +
                (low != null ? "low=" + low + ", " : "") +
                (volume != null ? "volume=" + volume + ", " : "") +
            "}";
    }

}
