import { Component, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-nuestra-flota',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './nuestra_flota.component.html',
  styleUrls: ['./nuestra_flota.component.css']
})
export class NuestraFlotaComponent implements OnInit, OnDestroy {
  isNavbarScrolled = false;
  isDarkMode = false;

  constructor(private renderer: Renderer2) {}

  ngOnInit(): void {
    this.checkScroll();

    // Check for saved theme preference
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  ngOnDestroy(): void {
    // Cleanup
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
  }

  toggleDarkMode(): void {
    this.isDarkMode = !this.isDarkMode;
    if (this.isDarkMode) {
      this.enableDarkMode();
      localStorage.setItem('theme', 'dark');
    } else {
      this.disableDarkMode();
      localStorage.setItem('theme', 'light');
    }
  }

  private enableDarkMode(): void {
    this.renderer.addClass(document.body, 'dark-theme');
    this.renderer.addClass(document.documentElement, 'dark-theme-active');
    window.dispatchEvent(new Event('darkmode-change'));
  }

  private disableDarkMode(): void {
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');
    window.dispatchEvent(new Event('darkmode-change'));
  }

  private checkScroll(): void {
    const heroElement = document.querySelector('.fleet-hero');
    if (heroElement) {
      const heroHeight = heroElement.clientHeight;
      this.isNavbarScrolled = window.scrollY > heroHeight - 100;
    } else {
      this.isNavbarScrolled = window.scrollY > 100;
    }
  }
}
