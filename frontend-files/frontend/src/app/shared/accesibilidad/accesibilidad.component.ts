import { Component, OnInit, OnDestroy, Renderer2 } from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-accesibilidad',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './accesibilidad.component.html',
  styleUrls: ['./accesibilidad.component.css']
})
export class AccesibilidadComponent implements OnInit, OnDestroy {

  isOpen = false;
  currentLevel = 0; // -2 to +4
  readonly MIN_LEVEL = -2;
  readonly MAX_LEVEL = 4;
  readonly STEP_PX = 2;
  readonly BASE_FONT_PX = 16;

  private readonly STORAGE_KEY = 'astranimbusA11yFontLevel';

  constructor(private renderer: Renderer2) {}

  ngOnInit(): void {
    const saved = localStorage.getItem(this.STORAGE_KEY);
    if (saved !== null) {
      this.currentLevel = parseInt(saved, 10);
      this.applyFontSize();
    }
  }

  ngOnDestroy(): void {
    this.renderer.removeStyle(document.documentElement, 'font-size');
  }

  togglePanel(): void {
    this.isOpen = !this.isOpen;
  }

  increase(): void {
    if (this.currentLevel < this.MAX_LEVEL) {
      this.currentLevel++;
      this.applyFontSize();
      this.save();
    }
  }

  decrease(): void {
    if (this.currentLevel > this.MIN_LEVEL) {
      this.currentLevel--;
      this.applyFontSize();
      this.save();
    }
  }

  reset(): void {
    this.currentLevel = 0;
    this.applyFontSize();
    this.save();
  }

  get currentFontSize(): number {
    return this.BASE_FONT_PX + this.currentLevel * this.STEP_PX;
  }

  get isMinLevel(): boolean {
    return this.currentLevel <= this.MIN_LEVEL;
  }

  get isMaxLevel(): boolean {
    return this.currentLevel >= this.MAX_LEVEL;
  }

  get isDefault(): boolean {
    return this.currentLevel === 0;
  }

  private applyFontSize(): void {
    this.renderer.setStyle(
      document.documentElement,
      'font-size',
      `${this.currentFontSize}px`
    );
  }

  private save(): void {
    localStorage.setItem(this.STORAGE_KEY, this.currentLevel.toString());
  }
}
